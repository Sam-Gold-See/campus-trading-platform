package samgoldsee.campus.trading.platform.service;

import jakarta.validation.Valid;
import samgoldsee.campus.trading.platform.dto.request.MarkSoldReq;
import samgoldsee.campus.trading.platform.dto.request.PublishItemReq;
import samgoldsee.campus.trading.platform.dto.request.SearchItemReq;
import samgoldsee.campus.trading.platform.dto.reponse.ItemResp;

import java.util.List;

/**
 * 需求/物品服务接口
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
public interface ItemService {

    /**
     * 发布需求/物品
     */
    Long publish(Long userId, @Valid PublishItemReq request);

    /**
     * 获取需求墙列表
     */
    List<ItemResp> getFeedList(Integer type, Integer page, Integer size);

    /**
     * 综合搜索
     */
    List<ItemResp> search(@Valid SearchItemReq request);

    /**
     * 获取我的发布列表
     */
    List<ItemResp> getMyItems(Long userId, Integer status, Integer page, Integer size);

    /**
     * 获取物品详情
     */
    ItemResp getDetail(Long id);

    /**
     * 擦亮帖文
     */
    void bump(Long userId, Long itemId);

    /**
     * 下架帖文
     */
    void offline(Long userId, Long itemId);

    /**
     * 标记已成交
     */
    void markSold(Long userId, Long itemId, @Valid MarkSoldReq request);
}