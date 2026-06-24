package samgoldsee.campus.trading.platform.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import samgoldsee.campus.trading.platform.config.JwtPropertiesConfig;
import samgoldsee.campus.trading.platform.dto.reponse.LoginResp;
import samgoldsee.campus.trading.platform.dto.reponse.UserProfileResp;
import samgoldsee.campus.trading.platform.dto.request.EditNicknameReq;
import samgoldsee.campus.trading.platform.dto.request.EditPasswordReq;
import samgoldsee.campus.trading.platform.dto.request.LoginReq;
import samgoldsee.campus.trading.platform.dto.request.RegisterReq;
import samgoldsee.campus.trading.platform.dto.request.SendRegisterCodeReq;
import samgoldsee.campus.trading.platform.entity.User;
import samgoldsee.campus.trading.platform.enums.EmailActionEnum;
import samgoldsee.campus.trading.platform.enums.IsAdminEnum;
import samgoldsee.campus.trading.platform.enums.UserStatusEnum;
import samgoldsee.campus.trading.platform.exception.BusinessException;
import samgoldsee.campus.trading.platform.mapper.UserMapper;
import samgoldsee.campus.trading.platform.security.JwtTokenProvider;
import samgoldsee.campus.trading.platform.service.UserService;
import samgoldsee.campus.trading.platform.util.EmailUtils;

import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 *
 * @author HuangChunXin
 * @date 2026/5/22 21:13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtPropertiesConfig jwtPropertiesConfig;

    @Value("${ctp.root.edu-email}")
    private String rootEduEmail;

    @Value("${ctp.root.password}")
    private String rootPassword;

    private static final String REGISTER_CODE_PREFIX = "register:email:";
    private static final long CODE_EXPIRE_MINUTES = 5;
    private static final long SEND_CODE_INTERVAL_SECONDS = 60;
    private static final String NICKNAME_COOLDOWN_PREFIX = "nickname:cooldown:";
    private static final long NICKNAME_COOLDOWN_DAYS = 30;
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    @PostConstruct
    @Override
    public void initAdminUser() {
        // 检查是否已存在管理员用户
        if (userMapper.findByEduEmail(rootEduEmail) != null) {
            log.info("Admin user already exists");
            return;
        }

        // 创建默认管理员用户
        User root = User.builder()
                .eduEmail(rootEduEmail)
                .passwordHash(passwordEncoder.encode(rootPassword))
                .nickname("root")
                .creditScore(100)
                .userStatus(UserStatusEnum.NORMAL.getCode())
                .isAdmin(IsAdminEnum.ADMIN.getCode())
                .build();
        userMapper.insertOrUpdate(root);
        log.info("Admin user initialized successfully");
    }

    @Override
    public LoginResp login(LoginReq request) {
        // 根据用户名查找用户
        User user = userMapper.findByEduEmail(request.getEduEmail());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("密码错误");
        }

        // 生成JWT Token
        String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()));
        Long expireTime = jwtTokenProvider.getExpirationTime();

        // 返回登录响应
        return LoginResp.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .token(token)
                .accessExpire(expireTime)
                .build();
    }

    @Override
    public void sendRegisterCode(SendRegisterCodeReq request) {
        String email = request.getEduEmail();

        // 校验邮箱格式（必须是 *.edu.cn）
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.edu\\.cn$")) {
            throw new BusinessException("必须是有效的.edu.cn教育邮箱");
        }

        // 检查邮箱是否已注册
        if (userMapper.findByEduEmail(email) != null) {
            throw new BusinessException("该邮箱已被注册");
        }

        // 限流检查：同一邮箱60秒内只能发送一次
        String rateLimitKey = REGISTER_CODE_PREFIX + "rate:" + email;
        Boolean isRateLimited = stringRedisTemplate.hasKey(rateLimitKey);
        if (Boolean.TRUE.equals(isRateLimited)) {
            throw new BusinessException("请勿频繁发送验证码，请60秒后再试");
        }

        // 生成6位随机验证码
        String code = String.format("%06d", (int) (Math.random() * 1000000));

        // 存储验证码到Redis，有效期5分钟
        String redisKey = REGISTER_CODE_PREFIX + email;
        stringRedisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 设置限流标记，有效期60秒
        stringRedisTemplate.opsForValue().set(rateLimitKey, "1", SEND_CODE_INTERVAL_SECONDS, TimeUnit.SECONDS);

        // 发送邮件
        try {
            EmailUtils.sendVerificationCode(email, EmailActionEnum.REGISTER, code);
            log.info("注册验证码发送成功，邮箱: {}", email);
        } catch (Exception e) {
            // 发送失败时删除Redis中的验证码
            stringRedisTemplate.delete(redisKey);
            stringRedisTemplate.delete(rateLimitKey);
            log.error("发送注册验证码失败，邮箱: {}", email, e);
            throw new BusinessException("验证码发送失败，请稍后重试");
        }
    }

    @Override
    public LoginResp register(RegisterReq request) {
        String email = request.getEduEmail();
        String verificationCode = request.getVerificationCode();
        String nickname = request.getNickname();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        // 校验两次密码是否一致
        if (!password.equals(confirmPassword)) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 验证验证码有效性
        String redisKey = REGISTER_CODE_PREFIX + email;
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            throw new BusinessException("验证码无效或已过期");
        }

        // 检查邮箱是否已注册
        if (userMapper.findByEduEmail(email) != null) {
            throw new BusinessException("该邮箱已被注册");
        }

        // 校验昵称唯一性
        if (userMapper.countByNickname(nickname) > 0) {
            throw new BusinessException("该昵称已被使用，请更换");
        }

        // 使用BCrypt对密码进行加盐哈希
        String passwordHash = passwordEncoder.encode(password);

        // 创建用户对象
        User user = User.builder()
                .eduEmail(email)
                .passwordHash(passwordHash)
                .nickname(nickname)
                .creditScore(100)
                .userStatus(UserStatusEnum.NORMAL.getCode())
                .isAdmin(IsAdminEnum.USER.getCode())
                .build();

        // 保存用户到数据库
        userMapper.insertOrUpdate(user);

        // 删除Redis中的验证码
        stringRedisTemplate.delete(redisKey);

        // 生成JWT Token
        User savedUser = userMapper.findByEduEmail(email);
        String token = jwtTokenProvider.generateToken(String.valueOf(savedUser.getId()));
        Long expireTime = jwtTokenProvider.getExpirationTime();

        log.info("用户注册成功，邮箱: {}, 用户ID: {}", email, savedUser.getId());

        // 返回注册响应
        return LoginResp.builder()
                .id(savedUser.getId())
                .nickname(savedUser.getNickname())
                .token(token)
                .accessExpire(expireTime)
                .build();
    }

    @Override
    public void editNickname(Long userId, EditNicknameReq request) {
        String newNickname = request.getNickname();

        // 检查冷却期：每30天仅能修改一次
        String cooldownKey = NICKNAME_COOLDOWN_PREFIX + userId;
        Boolean inCooldown = stringRedisTemplate.hasKey(cooldownKey);
        if (Boolean.TRUE.equals(inCooldown)) {
            throw new BusinessException("昵称30天内仅能修改一次，当前仍在冷却期内");
        }

        // 获取当前用户信息
        User currentUser = userMapper.findById(userId);
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 昵称未变化则无需更新
        if (newNickname.equals(currentUser.getNickname())) {
            return;
        }

        // 校验昵称唯一性
        if (userMapper.countByNickname(newNickname) > 0) {
            throw new BusinessException("该昵称已被使用，请更换");
        }

        // 更新昵称
        userMapper.updateNickname(userId, newNickname);

        // 设置冷却期
        stringRedisTemplate.opsForValue().set(
                cooldownKey, "1",
                NICKNAME_COOLDOWN_DAYS, TimeUnit.DAYS);

        log.info("昵称修改成功，用户ID: {}, 新昵称: {}", userId, newNickname);
    }

    @Override
    public void editPassword(Long userId, EditPasswordReq request) {
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();
        String confirmNewPassword = request.getConfirmNewPassword();

        // ① 两次新密码是否一致
        if (!newPassword.equals(confirmNewPassword)) {
            throw new BusinessException("两次输入的新密码不一致");
        }

        // ② 新密码不能和旧密码一样
        if (oldPassword.equals(newPassword)) {
            throw new BusinessException("新密码不能与旧密码相同");
        }

        // ③ 获取当前用户，验证旧密码是否正确
        User currentUser = userMapper.findById(userId);
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, currentUser.getPasswordHash())) {
            throw new BusinessException("旧密码输入错误");
        }

        // ④ 加密新密码并更新
        String newPasswordHash = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(userId, newPasswordHash);

        log.info("密码修改成功，用户ID: {}", userId);
    }

    @Override
    public UserProfileResp getProfile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserProfileResp.builder()
                .id(user.getId())
                .eduEmail(user.getEduEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .creditScore(user.getCreditScore())
                .userStatus(user.getUserStatus())
                .isAdmin(user.getIsAdmin())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public UserProfileResp getPublicProfile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 邮箱脱敏处理
        String maskedEmail = maskEmail(user.getEduEmail());

        return UserProfileResp.builder()
                .id(user.getId())
                .eduEmail(maskedEmail)
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .creditScore(user.getCreditScore())
                .userStatus(user.getUserStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            Long remainingTime = jwtTokenProvider.getExpirationTime(token);
            if (remainingTime > 0) {
                stringRedisTemplate.opsForValue().set(
                        TOKEN_BLACKLIST_PREFIX + token, "",
                        remainingTime, TimeUnit.SECONDS);
                log.info("用户登出成功，Token已加入黑名单");
            }
        }
    }

    /**
     * 邮箱脱敏处理
     * 例如：zhangsan@scnu.edu.cn -> z***@scnu.edu.cn
     */
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email;
        }
        String prefix = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        String maskedPrefix = prefix.length() <= 2 ? prefix.substring(0, 1) + "***" : prefix.substring(0, 2) + "***";
        return maskedPrefix + domain;
    }
}