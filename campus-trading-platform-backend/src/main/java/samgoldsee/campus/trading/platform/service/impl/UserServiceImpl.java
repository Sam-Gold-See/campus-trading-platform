package samgoldsee.campus.trading.platform.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import samgoldsee.campus.trading.platform.dto.reponse.LoginResp;
import samgoldsee.campus.trading.platform.dto.request.LoginReq;
import samgoldsee.campus.trading.platform.entity.User;
import samgoldsee.campus.trading.platform.enums.IsAdminEnum;
import samgoldsee.campus.trading.platform.enums.UserStatusEnum;
import samgoldsee.campus.trading.platform.exception.BusinessException;
import samgoldsee.campus.trading.platform.mapper.UserMapper;
import samgoldsee.campus.trading.platform.security.JwtTokenProvider;
import samgoldsee.campus.trading.platform.service.UserService;

/**
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

	@Value("${ctp.root.edu-email}")
	private String rootEduEmail;

	@Value("${ctp.root.password}")
	private String rootPassword;

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
}
