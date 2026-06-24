package samgoldsee.campus.trading.platform.service;

import samgoldsee.campus.trading.platform.dto.reponse.CategoryResp;
import samgoldsee.campus.trading.platform.dto.reponse.ConfigDictResp;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
public interface CategoryService {

    /**
     * 获取所有分类
     */
    List<CategoryResp> getAllCategories();

    /**
     * 获取配置字典（分类、校区、价格区间）
     */
    ConfigDictResp getConfigDict();
}