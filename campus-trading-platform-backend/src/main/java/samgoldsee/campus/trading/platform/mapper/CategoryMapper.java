package samgoldsee.campus.trading.platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import samgoldsee.campus.trading.platform.entity.Category;

import java.util.List;

/**
 * 分类字典Mapper
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Mapper
public interface CategoryMapper {

    /**
     * 查询所有分类（按排序权重）
     */
    @Select("""
            SELECT * FROM `category` ORDER BY `sort_order` ASC
            """)
    List<Category> findAll();

    /**
     * 根据ID查询分类
     */
    @Select("""
            SELECT * FROM `category` WHERE `id` = #{id}
            """)
    Category findById(Integer id);
}