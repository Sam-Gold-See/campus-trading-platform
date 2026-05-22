package samgoldsee.campus.trading.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author HuangChunXin
 * @date 2026/5/22 21:08
 */
@Data
public class LoginReq {

	/**
	 * 教育邮箱
	 */
	@NotBlank(message = "教育邮箱账号不能为空")
	private String eduEmail;

	/**
	 * 密码
	 */
	@NotBlank(message = "密码不能为空")
	private String password;
}
