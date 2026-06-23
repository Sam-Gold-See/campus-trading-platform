package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResp {

	private Long id;
	private Long itemId;
	/** 1=好评, 0=中评, -1=差评 */
	private Integer ratingType;
	/** 标签，逗号分隔 */
	private String tags;
	/** 文字短评 */
	private String content;
	/** 评价人昵称（脱敏） */
	private String reviewerNickname;
	private LocalDateTime createdAt;
}
