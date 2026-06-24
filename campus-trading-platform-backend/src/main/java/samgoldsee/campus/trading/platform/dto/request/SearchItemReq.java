package samgoldsee.campus.trading.platform.dto.request;

import lombok.Data;

/**
 * 搜索筛选请求DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
public class SearchItemReq {

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 交易类型 (1求购, 2转让)
     */
    private Integer type;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 校区
     */
    private String campus;

    /**
     * 价格区间标识
     */
    private String priceRange;

    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 获取偏移量（用于LIMIT）
     */
    public Integer getOffset() {
        return (page - 1) * size;
    }
}