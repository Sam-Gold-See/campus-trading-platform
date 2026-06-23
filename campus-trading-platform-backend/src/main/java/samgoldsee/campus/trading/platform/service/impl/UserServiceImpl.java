package samgoldsee.campus.trading.platform.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import samgoldsee.campus.trading.platform.config.JwtPropertiesConfig;
import samgoldsee.campus.trading.platform.constant.AccountConstant;
import samgoldsee.campus.trading.platform.constant.MessageConstant;
import samgoldsee.campus.trading.platform.constant.RedisConstant;
import samgoldsee.campus.trading.platform.dto.reponse.LoginResp;
import samgoldsee.campus.trading.platform.dto.reponse.ReviewListResp;
import samgoldsee.campus.trading.platform.dto.reponse.ReviewResp;
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
import samgoldsee.campus.trading.platform.mapper.ReviewMapper;
import samgoldsee.campus.trading.platform.mapper.UserMapper;
import samgoldsee.campus.trading.platform.security.JwtTokenProvider;
import samgoldsee.campus.trading.platform.service.UserService;
import samgoldsee.campus.trading.platform.util.EmailUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author HuangChunXin
 * @date 2026/5/22 21:13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserMapper userMapper;
	private final ReviewMapper reviewMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final StringRedisTemplate stringRedisTemplate;
	private final JwtPropertiesConfig jwtPropertiesConfig;

	@Value("${ctp.root.edu-email}")
	private String rootEduEmail;

	@Value("${ctp.root.password}")
	private String rootPassword;

	@PostConstruct
	@Override
	public void initAdminUser() {
		if (userMapper.findByEduEmail(rootEduEmail) != null) {
			log.info("Admin user already exists");
			return;
		}

		User root = User.builder()
				.eduEmail(rootEduEmail)
				.passwordHash(passwordEncoder.encode(rootPassword))
				.nickname(AccountConstant.ROOT_NICKNAME)
				.userStatus(UserStatusEnum.NORMAL.getCode())
				.isAdmin(IsAdminEnum.ADMIN.getCode())
				.build();
		userMapper.insertOrUpdate(root);
		log.info("Admin user initialized successfully");
	}

	@Override
	public LoginResp login(LoginReq request) {
		User user = userMapper.findByEduEmail(request.getEduEmail());
		if (user == null) {
			throw new BusinessException(MessageConstant.USER_NOT_FOUND);
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new BusinessException(MessageConstant.PASSWORD_ERROR);
		}

		String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()));
		Long expireTime = jwtTokenProvider.getExpirationTime();

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

		if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.edu\\.cn$")) {
			throw new BusinessException(MessageConstant.EMAIL_NOT_EDU);
		}

		if (userMapper.findByEduEmail(email) != null) {
			throw new BusinessException(MessageConstant.EMAIL_ALREADY_REGISTERED);
		}

		String rateLimitKey = RedisConstant.REGISTER_RATE_LIMIT_PREFIX + email;
		Boolean isRateLimited = stringRedisTemplate.hasKey(rateLimitKey);
		if (Boolean.TRUE.equals(isRateLimited)) {
			throw new BusinessException(MessageConstant.CODE_SEND_TOO_FREQUENT);
		}

		String code = String.format("%06d", (int) (Math.random() * 1000000));

		String redisKey = RedisConstant.REGISTER_CODE_PREFIX + email;
		stringRedisTemplate.opsForValue().set(redisKey, code,
				AccountConstant.VERIFICATION_CODE_TTL, TimeUnit.MINUTES);

		stringRedisTemplate.opsForValue().set(rateLimitKey, "1",
				AccountConstant.SEND_CODE_INTERVAL_SECONDS, TimeUnit.SECONDS);

		try {
			EmailUtils.sendVerificationCode(email, EmailActionEnum.REGISTER, code);
			log.info("注册验证码发送成功，邮箱: {}", email);
		} catch (Exception e) {
			stringRedisTemplate.delete(redisKey);
			stringRedisTemplate.delete(rateLimitKey);
			log.error("发送注册验证码失败，邮箱: {}", email, e);
			throw new BusinessException(MessageConstant.CODE_SEND_FAILED);
		}
	}

	@Override
	public LoginResp register(RegisterReq request) {
		String email = request.getEduEmail();
		String verificationCode = request.getVerificationCode();
		String nickname = request.getNickname();
		String password = request.getPassword();
		String confirmPassword = request.getConfirmPassword();

		if (!password.equals(confirmPassword)) {
			throw new BusinessException(MessageConstant.PASSWORD_NOT_MATCH);
		}

		String redisKey = RedisConstant.REGISTER_CODE_PREFIX + email;
		String storedCode = stringRedisTemplate.opsForValue().get(redisKey);
		if (storedCode == null || !storedCode.equals(verificationCode)) {
			throw new BusinessException(MessageConstant.CODE_INVALID);
		}

		if (userMapper.findByEduEmail(email) != null) {
			throw new BusinessException(MessageConstant.EMAIL_ALREADY_REGISTERED);
		}

		if (userMapper.countByNickname(nickname) > 0) {
			throw new BusinessException(MessageConstant.NICKNAME_TAKEN);
		}

		String passwordHash = passwordEncoder.encode(password);

		User user = User.builder()
				.eduEmail(email)
				.passwordHash(passwordHash)
				.nickname(nickname)
				.creditScore(AccountConstant.INITIAL_CREDIT_SCORE)
				.userStatus(UserStatusEnum.NORMAL.getCode())
				.isAdmin(IsAdminEnum.USER.getCode())
				.build();

		userMapper.insertOrUpdate(user);

		stringRedisTemplate.delete(redisKey);

		User savedUser = userMapper.findByEduEmail(email);
		String token = jwtTokenProvider.generateToken(String.valueOf(savedUser.getId()));
		Long expireTime = jwtTokenProvider.getExpirationTime();

		log.info("用户注册成功，邮箱: {}, 用户ID: {}", email, savedUser.getId());

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

		String cooldownKey = RedisConstant.NICKNAME_COOLDOWN_PREFIX + userId;
		Boolean inCooldown = stringRedisTemplate.hasKey(cooldownKey);
		if (Boolean.TRUE.equals(inCooldown)) {
			throw new BusinessException(MessageConstant.NICKNAME_COOLDOWN);
		}

		User currentUser = userMapper.findById(userId);
		if (currentUser == null) {
			throw new BusinessException(MessageConstant.USER_NOT_FOUND);
		}

		if (newNickname.equals(currentUser.getNickname())) {
			return;
		}

		if (userMapper.countByNickname(newNickname) > 0) {
			throw new BusinessException(MessageConstant.NICKNAME_TAKEN);
		}

		userMapper.updateNickname(userId, newNickname);

		stringRedisTemplate.opsForValue().set(
				cooldownKey, "1",
				AccountConstant.NICKNAME_COOLDOWN_DAYS, TimeUnit.DAYS);

		log.info("昵称修改成功，用户ID: {}, 新昵称: {}", userId, newNickname);
	}

	@Override
	public void editPassword(Long userId, EditPasswordReq request) {
		String oldPassword = request.getOldPassword();
		String newPassword = request.getNewPassword();
		String confirmNewPassword = request.getConfirmNewPassword();

		if (!newPassword.equals(confirmNewPassword)) {
			throw new BusinessException(MessageConstant.NEW_PASSWORD_NOT_MATCH);
		}

		if (oldPassword.equals(newPassword)) {
			throw new BusinessException(MessageConstant.NEW_PASSWORD_SAME_AS_OLD);
		}

		User currentUser = userMapper.findById(userId);
		if (currentUser == null) {
			throw new BusinessException(MessageConstant.USER_NOT_FOUND);
		}
		if (!passwordEncoder.matches(oldPassword, currentUser.getPasswordHash())) {
			throw new BusinessException(MessageConstant.OLD_PASSWORD_ERROR);
		}

		String newPasswordHash = passwordEncoder.encode(newPassword);
		userMapper.updatePassword(userId, newPasswordHash);

		log.info("密码修改成功，用户ID: {}", userId);
	}

	@Override
	public UserProfileResp getProfile(Long userId) {
		User user = userMapper.findById(userId);
		if (user == null) {
			throw new BusinessException(MessageConstant.USER_NOT_FOUND);
		}
		return UserProfileResp.builder()
				.id(user.getId())
				.eduEmail(maskEmail(user.getEduEmail()))
				.nickname(user.getNickname())
				.avatarUrl(user.getAvatarUrl())
				.creditScore(user.getCreditScore())
				.creditBadge(getCreditBadge(user.getCreditScore()))
				.userStatus(user.getUserStatus())
				.isAdmin(user.getIsAdmin())
				.createdAt(user.getCreatedAt())
				.build();
	}

	/** 邮箱脱敏：t***6@m.scnu.edu.cn */
	private String maskEmail(String email) {
		if (email == null || !email.contains("@")) {
			return email;
		}
		String[] parts = email.split("@");
		String local = parts[0];
		String domain = parts[1];
		if (local.length() <= 1) {
			return email;
		}
		return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + domain;
	}

	/** 信用分→徽章等级 */
	private String getCreditBadge(Integer score) {
		if (score == null) return "GRAY";
		if (score < 60)  return "RED";
		if (score <= 80) return "GRAY";
		if (score <= 100) return "BLUE";
		return "GOLD";
	}

	@Override
	public void logout(String token) {
		stringRedisTemplate.opsForValue().set(
				RedisConstant.TOKEN_BLACKLIST_PREFIX + token, "",
				jwtPropertiesConfig.getExpire(), TimeUnit.SECONDS);
		log.info("用户登出成功，Token已加入黑名单");
	}

	@Override
	public ReviewListResp getReviews(Long targetUserId, int page, int size) {
		User targetUser = userMapper.findById(targetUserId);
		if (targetUser == null) {
			throw new BusinessException(MessageConstant.USER_NOT_FOUND);
		}

		int offset = (page - 1) * size;
		List<ReviewResp> list = reviewMapper.findByRevieweeId(targetUserId, offset, size);
		long total = reviewMapper.countByRevieweeId(targetUserId);
		ReviewMapper.ReviewStats stats = reviewMapper.selectStats(targetUserId);

		return ReviewListResp.builder()
				.list(list)
				.total(total)
				.page(page)
				.size(size)
				.goodCount(stats.getGoodCount())
				.neutralCount(stats.getNeutralCount())
				.badCount(stats.getBadCount())
				.build();
	}
}
