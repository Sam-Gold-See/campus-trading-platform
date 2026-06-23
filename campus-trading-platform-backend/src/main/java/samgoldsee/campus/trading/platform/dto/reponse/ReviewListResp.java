package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResp {

	private List<ReviewResp> list;
	private Long total;
	private Integer page;
	private Integer size;
	/** 评价统计 */
	private Long goodCount;
	private Long neutralCount;
	private Long badCount;
}
