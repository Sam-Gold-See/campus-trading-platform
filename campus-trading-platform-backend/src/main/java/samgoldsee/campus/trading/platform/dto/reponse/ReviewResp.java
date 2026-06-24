package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价响应DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
@Builder
public class ReviewResp {

    /**
     * 评价ID
     */
    private Long id;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 评价人ID
     */
    private Long reviewerId;

    /**
     * 评价人昵称
     */
    private String reviewerNickname;

    /**
     * 被评价人ID
     */
    private Long revieweeId;

    /**
     * 评价性质 (1好评, 0中评, -1差评)
     */
    private Integer ratingType;

    /**
     * 评价标签
     */
    private List<String> tags;

    /**
     * 文字短评
     */
    private String content;

    /**
     * 评价时间
     */
    private LocalDateTime createdAt;
}