package samgoldsee.campus.trading.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author HuangChunXin
 * @date 2026/6/4
 */
@Data
public class EditNicknameReq {

	@NotBlank(message = "昵称不能为空")
	@Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5_]{4,16}$", message = "昵称需4-16位，不可包含特殊字符")
	private String nickname;
}
