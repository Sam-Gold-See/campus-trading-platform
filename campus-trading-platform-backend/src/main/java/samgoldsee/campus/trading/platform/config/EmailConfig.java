package samgoldsee.campus.trading.platform.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 邮件配置类
 *
 * @author HuangChunXin
 * @date 2026/5/23 09:23
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {

	// 邮件服务器地址
	private String host;

	// 邮件服务器端口
	private int port;

	// 邮箱账号
	private String username;

	// 邮箱授权码
	private String password;

	// 发送邮件协议
	private String protocol;

	// 默认编码
	private String defaultEncoding;
}
