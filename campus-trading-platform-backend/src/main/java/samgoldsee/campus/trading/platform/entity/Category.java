package samgoldsee.campus.trading.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类字典实体类
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

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