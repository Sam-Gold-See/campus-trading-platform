package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 需求/物品响应DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
@Builder
public class ItemResp {

    /**
     * 物品ID
     */
    private Long id;

    /**
     * 发布者ID
     */
    private Long userId;

    /**
     * 发布者昵称
     */
    private String userNickname;

    /**
     * 发布者信用分
     */
    private Integer userCreditScore;

    /**
     * 交易类型 (1求购, 2转让)
     */
    private Integer type;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 校区/楼栋位置
     */
    private String campus;

    /**
     * 期望价格
     */
    private BigDecimal price;

    /**
     * 纯文本描述
     */
    private String content;

    /**
     * 补充实物图URL
     */
    private String imageUrl;

    /**
     * 状态 (0展示中, 1已成交, 2已失效, 3已删除)
     */
    private Integer itemStatus;

    /**
     * 发布时间
     */
    private LocalDateTime createdAt;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;
}