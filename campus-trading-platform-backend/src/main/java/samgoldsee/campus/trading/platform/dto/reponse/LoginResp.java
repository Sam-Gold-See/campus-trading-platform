package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HuangChunXin
 * @date 2026/5/22 21:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResp {

	/**
	 * 用户ID
	 */
	private Long id;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * JWT Token
	 */
	private String token;

	/**
	 * Token过期时间戳（秒）
	 */
	private Long accessExpire;
}
