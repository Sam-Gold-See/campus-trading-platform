package samgoldsee.campus.trading.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import samgoldsee.campus.trading.platform.dto.reponse.CategoryResp;
import samgoldsee.campus.trading.platform.dto.reponse.ConfigDictResp;
import samgoldsee.campus.trading.platform.entity.Category;
import samgoldsee.campus.trading.platform.mapper.CategoryMapper;
import samgoldsee.campus.trading.platform.service.CategoryService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    /**
     * 预定义的校区列表
     */
    private static final List<String> CAMPUSES = Arrays.asList(
            "南校区",
            "北校区",
            "东校区",
            "西校区",
            "本部"
    );

    /**
     * 预定义的价格区间
     */
    private static final List<String> PRICE_RANGES = Arrays.asList(
            "50元以下",
            "50-200元",
            "200元以上",
            "面议"
    );

    @Override
    public List<CategoryResp> getAllCategories() {
        List<Category> categories = categoryMapper.findAll();
        return categories.stream()
                .map(c -> CategoryResp.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .sortOrder(c.getSortOrder())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ConfigDictResp getConfigDict() {
        List<CategoryResp> categories = getAllCategories();
        return ConfigDictResp.builder()
                .categories(categories)
                .campuses(CAMPUSES)
                .priceRanges(PRICE_RANGES)
                .build();
    }
}