package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 配置字典响应DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
@Builder
public class ConfigDictResp {

    /**
     * 分类列表
     */
    private List<CategoryResp> categories;

    /**
     * 校区列表
     */
    private List<String> campuses;

    /**
     * 价格区间列表
     */
    private List<String> priceRanges;
}