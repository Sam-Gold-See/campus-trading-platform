package samgoldsee.campus.trading.platform.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发布需求/物品请求DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
public class PublishItemReq {

    /**
     * 交易类型 (1求购, 2转让)
     */
    @NotNull(message = "交易类型不能为空")
    @Min(value = 1, message = "交易类型无效")
    @Max(value = 2, message = "交易类型无效")
    private Integer type;

    /**
     * 分类ID
     */
    @NotNull(message = "分类不能为空")
    private Integer categoryId;

    /**
     * 校区/楼栋位置
     */
    @NotBlank(message = "校区不能为空")
    @Size(max = 50, message = "校区名称不能超过50字")
    private String campus;

    /**
     * 期望价格（可选，NULL代表面议）
     */
    @DecimalMin(value = "0", message = "价格不能为负数")
    private BigDecimal price;

    /**
     * 纯文本描述
     */
    @NotBlank(message = "描述不能为空")
    @Size(min = 1, max = 200, message = "描述长度需在1-200字之间")
    private String content;

    /**
     * 补充实物图URL（可选，最多1张）
     */
    @Size(max = 255, message = "图片URL过长")
    private String imageUrl;
}