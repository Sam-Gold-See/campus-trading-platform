package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemResp {

	private Long id;
	private Long userId;
	private Long matchedUserId;
	private String userNickname;
	private Integer userCreditScore;
	private Integer type;
	private Integer categoryId;
	private String categoryName;
	private String campus;
	private BigDecimal price;
	private String content;
	private String imageUrl;
	private Integer itemStatus;
	private LocalDateTime createdAt;
	private LocalDateTime expireAt;
}
