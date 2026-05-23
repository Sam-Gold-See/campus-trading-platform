package samgoldsee.campus.trading.platform.util;

import org.apache.commons.mail.HtmlEmail;
import org.springframework.stereotype.Component;
import samgoldsee.campus.trading.platform.config.EmailConfig;
import samgoldsee.campus.trading.platform.constant.AccountConstant;
import samgoldsee.campus.trading.platform.constant.MessageConstant;
import samgoldsee.campus.trading.platform.enums.EmailActionEnum;
import samgoldsee.campus.trading.platform.exception.BusinessException;

/**
 * @author HuangChunXin
 * @date 2026/5/23 09:21
 */
@Component
public class EmailUtils {

	private static EmailConfig emailConfig;

	public EmailUtils(EmailConfig emailConfig) {
		EmailUtils.emailConfig = emailConfig;
	}

	/**
	 * 发送验证码邮件
	 */
	public static void sendVerificationCode(String toEmail, EmailActionEnum emailActionEnum, String verificationCode) {
		try {
			HtmlEmail email = new HtmlEmail();
			email.setHostName(emailConfig.getHost()); // SMTP服务器
			email.setCharset(emailConfig.getDefaultEncoding());
			email.setAuthentication(emailConfig.getUsername(), emailConfig.getPassword()); // 认证信息
			email.setFrom(emailConfig.getUsername(), "campus_trading"); // 发件人
			email.setSSLOnConnect(true); // 启用SSL
			email.addTo(toEmail); // 收件人
			email.setSubject(emailActionEnum.getDescription() + "验证码");
			email.setMsg("尊敬的用户，您好！您本次操作的验证码是：" + verificationCode + "\n请在" + AccountConstant.VERIFICATION_CODE_TTL + "分钟内尽快使用。");
			email.send();
		} catch (Exception e) {
			throw new BusinessException(MessageConstant.SEND_EMAIL_FAIL);
		}
	}
}
