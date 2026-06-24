package samgoldsee.campus.trading.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 提交评价请求DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
public class SubmitReviewReq {

    /**
     * 物品ID
     */
    @NotNull(message = "物品不能为空")
    private Long itemId;

    /**
     * 被评价人ID
     */
    @NotNull(message = "被评价人不能为空")
    private Long revieweeId;

    /**
     * 评价性质 (1好评, 0中评, -1差评)
     */
    @NotNull(message = "评价性质不能为空")
    private Integer ratingType;

    /**
     * 评价标签
     */
    private List<String> tags;

    /**
     * 文字短评
     */
    @Size(max = 50, message = "评价内容不能超过50字")
    private String content;
}