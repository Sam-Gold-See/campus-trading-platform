package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

/**
 * 分类响应DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
@Builder
public class CategoryResp {

    /**
     * 分类ID
     */
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序权重
     */
    private Integer sortOrder;
}