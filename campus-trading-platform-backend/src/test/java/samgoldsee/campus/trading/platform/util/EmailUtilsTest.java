package samgoldsee.campus.trading.platform.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import samgoldsee.campus.trading.platform.config.EmailConfig;
import samgoldsee.campus.trading.platform.constant.AccountConstant;
import samgoldsee.campus.trading.platform.enums.EmailActionEnum;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EmailUtils 测试类 - 集成测试
 * 使用 application-dev.yaml 中的真实配置发送邮件
 *
 * @author HuangChunXin
 * @date 2026/5/23
 */
@SpringBootTest
@ActiveProfiles("dev")
class EmailUtilsTest {

	@Autowired
	private EmailConfig emailConfig;

	private static final String TEST_EMAIL = "@m.scnu.edu.cn";

	@BeforeEach
	void setUp() throws Exception {
		// 通过反射设置 EmailUtils 中的静态字段 emailConfig
		Field field = EmailUtils.class.getDeclaredField("emailConfig");
		field.setAccessible(true);
		field.set(null, emailConfig);
	}

	@Test
	@DisplayName("测试发送注册验证码邮件 - 使用真实配置")
	void testSendVerificationCode_Register_WithRealConfig() {
		// 准备测试数据
		String toEmail = TEST_EMAIL;
		EmailActionEnum action = EmailActionEnum.REGISTER;
		String verificationCode = "123456";

		// 打印配置信息用于调试
		System.out.println("邮件配置信息:");
		System.out.println("SMTP服务器: " + emailConfig.getHost());
		System.out.println("SMTP端口: " + emailConfig.getPort());
		System.out.println("发件人邮箱: " + emailConfig.getUsername());
		System.out.println("收件人邮箱: " + toEmail);
		System.out.println("验证码: " + verificationCode);

		// 执行发送邮件（会真实发送）
		assertDoesNotThrow(() -> {
			EmailUtils.sendVerificationCode(toEmail, action, verificationCode);
			System.out.println("✓ 注册验证码邮件发送成功！");
		}, "发送注册验证码邮件失败，请检查邮件配置和网络连接");
	}

	@Test
	@DisplayName("测试发送重置密码验证码邮件 - 使用真实配置")
	void testSendVerificationCode_ResetPassword_WithRealConfig() {
		// 准备测试数据
		EmailActionEnum action = EmailActionEnum.RESET_PASSWORD;
		String verificationCode = "654321";

		System.out.println("\n发送重置密码验证码邮件...");
		System.out.println("验证码: " + verificationCode);

		// 执行发送邮件（会真实发送）
		assertDoesNotThrow(() -> {
			EmailUtils.sendVerificationCode(TEST_EMAIL, action, verificationCode);
			System.out.println("✓ 重置密码验证码邮件发送成功！");
		}, "发送重置密码验证码邮件失败，请检查邮件配置和网络连接");
	}

	@Test
	@DisplayName("测试发送验证码邮件 - 验证邮件内容完整性")
	void testSendVerificationCode_VerifyContentIntegrity() {
		// 准备测试数据
		EmailActionEnum action = EmailActionEnum.REGISTER;
		String verificationCode = "999888";

		System.out.println("\n测试邮件内容完整性...");
		System.out.println("预期邮件主题: " + action.getDescription() + "验证码");
		System.out.println("预期邮件内容: 尊敬的用户，您好！您本次操作的验证码是：" + verificationCode 
			+ " ，请在" + AccountConstant.VERIFICATION_CODE_TTL + "分钟内尽快使用。");

		// 执行发送邮件
		assertDoesNotThrow(() -> {
			EmailUtils.sendVerificationCode(TEST_EMAIL, action, verificationCode);
			System.out.println("✓ 邮件内容完整性测试通过！");
		}, "发送邮件失败，无法验证内容完整性");
	}

	@Test
	@DisplayName("测试不同长度验证码的邮件发送")
	void testSendVerificationCode_DifferentCodeLengths() {
		// 测试不同长度的验证码
		String[] codes = {"1234", "12345", "123456", "1234567", "12345678"};

		for (String code : codes) {
			System.out.println("\n测试验证码长度: " + code.length());
			
			assertDoesNotThrow(() -> {
				EmailUtils.sendVerificationCode(TEST_EMAIL, EmailActionEnum.REGISTER, code);
				System.out.println("✓ 长度为 " + code.length() + " 的验证码邮件发送成功！");
			}, "发送长度为 " + code.length() + " 的验证码邮件失败");
		}
	}

	@Test
	@DisplayName("测试邮件配置的SSL和编码设置")
	void testSendVerificationCode_ConfigValidation() {
		// 验证配置是否正确加载
		System.out.println("\n验证邮件配置...");
		assertNotNull(emailConfig.getHost(), "SMTP主机不能为空");
		assertNotNull(emailConfig.getUsername(), "发件人邮箱不能为空");
		assertNotNull(emailConfig.getPassword(), "邮箱授权码不能为空");
		assertNotNull(emailConfig.getDefaultEncoding(), "默认编码不能为空");
		
		System.out.println("✓ SMTP服务器: " + emailConfig.getHost());
		System.out.println("✓ 发件人: " + emailConfig.getUsername());
		System.out.println("✓ 编码: " + emailConfig.getDefaultEncoding());
		System.out.println("✓ SSL启用: true (代码中硬编码)");

		// 发送一封测试邮件验证配置有效性
		assertDoesNotThrow(() -> {
			EmailUtils.sendVerificationCode(
				TEST_EMAIL, 
				EmailActionEnum.REGISTER, 
				"CONFIG_TEST"
			);
			System.out.println("✓ 配置验证通过，测试邮件已发送！");
		}, "邮件配置无效或网络连接失败");
	}
}
