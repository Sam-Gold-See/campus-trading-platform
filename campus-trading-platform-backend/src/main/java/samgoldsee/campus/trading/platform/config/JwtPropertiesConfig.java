package samgoldsee.campus.trading.platform.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HuangChunXin
 * @date 2026/5/22 10:47
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtPropertiesConfig {

	/**
	 * JWT签名密钥
	 */
	private String secret = "";

	/**
	 * Token过期时间
	 */
	private Long expiration = 86400L;
}
