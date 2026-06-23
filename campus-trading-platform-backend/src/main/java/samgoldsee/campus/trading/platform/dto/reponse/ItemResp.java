package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemResp {

	private Long id;
	private Integer type;
	private Integer categoryId;
	private String categoryName;
	private String campus;
	private BigDecimal price;
	private String content;
	private String imageUrl;
	private Integer itemStatus;
	private Long matchedUserId;
	private LocalDateTime createdAt;
	private LocalDateTime expireAt;
}
