package samgoldsee.campus.trading.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 交易评价实体类
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    /**
     * 评价流水ID
     */
    private Long id;

    /**
     * 关联的交易物品ID
     */
    private Long itemId;

    /**
     * 评价人ID
     */
    private Long reviewerId;

    /**
     * 被评价人ID
     */
    private Long revieweeId;

    /**
     * 评价性质 (1好评加分, 0中评, -1差评扣分)
     */
    private Integer ratingType;

    /**
     * 结构化标签（逗号分隔）
     */
    private String tags;

    /**
     * 文字短评
     */
    private String content;

    /**
     * 评价时间
     */
    private LocalDateTime createdAt;

    // ========== 关联查询字段（非数据库字段） ==========

    /**
     * 评价人昵称（关联查询）
     */
    private String reviewerNickname;
}