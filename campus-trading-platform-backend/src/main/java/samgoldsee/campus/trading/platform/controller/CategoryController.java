package samgoldsee.campus.trading.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import samgoldsee.campus.trading.platform.common.CommonResult;
import samgoldsee.campus.trading.platform.dto.reponse.CategoryResp;
import samgoldsee.campus.trading.platform.dto.reponse.ConfigDictResp;
import samgoldsee.campus.trading.platform.service.CategoryService;

import java.util.List;

/**
 * 分类Controller
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public CommonResult<List<CategoryResp>> getAllCategories() {
        List<CategoryResp> categories = categoryService.getAllCategories();
        return CommonResult.ok(categories);
    }

    /**
     * 获取配置字典（分类、校区、价格区间）
     */
    @GetMapping("/dict")
    public CommonResult<ConfigDictResp> getConfigDict() {
        ConfigDictResp dict = categoryService.getConfigDict();
        return CommonResult.ok(dict);
    }
}