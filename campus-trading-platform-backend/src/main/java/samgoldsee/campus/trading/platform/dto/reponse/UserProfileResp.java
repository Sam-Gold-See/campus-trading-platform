package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResp {

	private Long id;
	private String eduEmail;
	private String nickname;
	private String avatarUrl;
	private Integer creditScore;
	private Integer userStatus;
	private Integer isAdmin;
	private LocalDateTime createdAt;
}
