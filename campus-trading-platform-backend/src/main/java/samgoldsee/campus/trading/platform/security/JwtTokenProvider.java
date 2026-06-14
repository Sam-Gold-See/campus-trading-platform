package samgoldsee.campus.trading.platform.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import samgoldsee.campus.trading.platform.config.JwtPropertiesConfig;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuangChunXin
 * @date 2026/5/22 11:01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtPropertiesConfig jwtPropertiesConfig;

	/**
	 * Authorization头名称
	 */
	public static final String AUTHORIZATION = "Authorization";

	/**
	 * 用户标识键名
	 */
	public static final String IDENTIFY = "campus_trading_platform";

	/**
	 * Bearer前缀
	 */
	public static final String BEARER_PREFIX = "Bearer ";

	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		String secret = jwtPropertiesConfig.getSecret();

		// 检查密钥是否配置且长度足够
		if (secret == null || secret.isEmpty()) {
			throw new IllegalStateException(
					"JWT secret 未配置，长度至少32个字符"
			);
		}

		// 检查密钥长度是否满足 HS256 最低要求（32字节）
		byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (secretBytes.length < 32) {
			throw new IllegalStateException(
					String.format(
							"JWT secret 长度不足，当前长度: %d 字节，至少需要 32 字节。",
							secretBytes.length
					)
			);
		}

		// 使用配置的密钥生成SecretKey
		this.secretKey = Keys.hmacShaKeyFor(secretBytes);
	}

	/**
	 * 生成JWT Token
	 */
	public String generateToken(String userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtPropertiesConfig.getExpire() * 1000);

		Map<String, Object> claims = new HashMap<>();
		claims.put(IDENTIFY, userId);

		return Jwts.builder()
				.claims(claims)
				.issuedAt(now)  // iat - 签发时间
				.expiration(expiryDate)  // exp - 过期时间
				.signWith(secretKey, Jwts.SIG.HS256)  // 使用HS256算法签名
				.compact();
	}

	/**
	 * 从Token中获取用户ID
	 */
	public String getUserIdFromToken(String token) {
		Claims claims = parseToken(token);
		if (claims == null) {
			return null;
		}
		return claims.get(IDENTIFY, String.class);
	}

	/**
	 * 验证Token是否有效
	 */
	public boolean validateToken(String token) {
		try {
			parseToken(token);
			return true;
		} catch (SecurityException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

	/**
	 * 从HTTP Authorization头中提取Token
	 */
	public String resolveToken(String bearerToken) {
		if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}
		return bearerToken;
	}

	/**
	 * 获取Token过期时间戳
	 */
	public Long getExpirationTime() {
		return System.currentTimeMillis() / 1000 + jwtPropertiesConfig.getExpire();
	}

	/**
	 * 根据Token获取过期时间戳
	 */
	public Long getExpirationTime(String token) {
		return parseToken(token).getExpiration().getTime() / 1000;
	}

	/**
	 * 解析Token获取Claims
	 */
	private Claims parseToken(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
