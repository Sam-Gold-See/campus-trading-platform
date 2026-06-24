package samgoldsee.campus.trading.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import samgoldsee.campus.trading.platform.common.CommonResult;
import samgoldsee.campus.trading.platform.dto.request.MarkSoldReq;
import samgoldsee.campus.trading.platform.dto.request.PublishItemReq;
import samgoldsee.campus.trading.platform.dto.request.SearchItemReq;
import samgoldsee.campus.trading.platform.dto.reponse.ItemResp;
import samgoldsee.campus.trading.platform.service.ItemService;

import java.util.List;

/**
 * 需求/物品Controller
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 发布需求/物品
     */
    @PostMapping("/publish")
    public CommonResult<Long> publish(@Valid @RequestBody PublishItemReq request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long itemId = itemService.publish(Long.valueOf(userId), request);
        return CommonResult.ok(itemId);
    }

    /**
     * 获取需求墙列表
     */
    @GetMapping("/feed")
    public CommonResult<List<ItemResp>> getFeedList(
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ItemResp> items = itemService.getFeedList(type, page, size);
        return CommonResult.ok(items);
    }

    /**
     * 综合搜索
     */
    @GetMapping("/search")
    public CommonResult<List<ItemResp>> search(SearchItemReq request) {
        List<ItemResp> items = itemService.search(request);
        return CommonResult.ok(items);
    }

    /**
     * 获取我的发布列表
     */
    @GetMapping("/my-list")
    public CommonResult<List<ItemResp>> getMyItems(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ItemResp> items = itemService.getMyItems(Long.valueOf(userId), status, page, size);
        return CommonResult.ok(items);
    }

    /**
     * 获取物品详情
     */
    @GetMapping("/{id}")
    public CommonResult<ItemResp> getDetail(@PathVariable Long id) {
        ItemResp item = itemService.getDetail(id);
        return CommonResult.ok(item);
    }

    /**
     * 擦亮帖文
     */
    @PutMapping("/{id}/bump")
    public CommonResult<Void> bump(@PathVariable Long id) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        itemService.bump(Long.valueOf(userId), id);
        return CommonResult.ok();
    }

    /**
     * 下架帖文
     */
    @PutMapping("/{id}/offline")
    public CommonResult<Void> offline(@PathVariable Long id) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        itemService.offline(Long.valueOf(userId), id);
        return CommonResult.ok();
    }

    /**
     * 标记已成交
     */
    @PutMapping("/{id}/sold")
    public CommonResult<Void> markSold(@PathVariable Long id, @Valid @RequestBody MarkSoldReq request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        itemService.markSold(Long.valueOf(userId), id, request);
        return CommonResult.ok();
    }
}