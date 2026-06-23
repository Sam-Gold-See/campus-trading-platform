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
	/** 信用分徽章等级：RED(<60) / GRAY(60-80) / BLUE(80-100) / GOLD(>100) */
	private String creditBadge;
}
