package samgoldsee.campus.trading.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 需求/物品实体类 - 核心交易池
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    /**
     * 帖文ID
     */
    private Long id;

    /**
     * 发布者ID
     */
    private Long userId;

    /**
     * 交易类型 (1求购, 2转让)
     */
    private Integer type;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 校区/楼栋位置
     */
    private String campus;

    /**
     * 期望价格（NULL代表面议）
     */
    private BigDecimal price;

    /**
     * 纯文本描述
     */
    private String content;

    /**
     * 补充实物图URL（仅限1张缩略图）
     */
    private String imageUrl;

    /**
     * 状态 (0展示中, 1已成交, 2已失效, 3已删除)
     */
    private Integer itemStatus;

    /**
     * 成交方用户ID
     */
    private Long matchedUserId;

    /**
     * 发布时间（擦亮功能更新此字段）
     */
    private LocalDateTime createdAt;

    /**
     * 过期时间（默认+14天）
     */
    private LocalDateTime expireAt;

    // ========== 关联查询字段（非数据库字段） ==========

    /**
     * 发布者昵称（关联查询）
     */
    private String userNickname;

    /**
     * 发布者信用分（关联查询）
     */
    private Integer userCreditScore;

    /**
     * 分类名称（关联查询）
     */
    private String categoryName;
}