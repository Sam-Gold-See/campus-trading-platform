package samgoldsee.campus.trading.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samgoldsee.campus.trading.platform.constant.ItemConstant;
import samgoldsee.campus.trading.platform.dto.request.MarkSoldReq;
import samgoldsee.campus.trading.platform.dto.request.PublishItemReq;
import samgoldsee.campus.trading.platform.dto.request.SearchItemReq;
import samgoldsee.campus.trading.platform.dto.reponse.ItemResp;
import samgoldsee.campus.trading.platform.entity.Category;
import samgoldsee.campus.trading.platform.entity.Item;
import samgoldsee.campus.trading.platform.entity.User;
import samgoldsee.campus.trading.platform.exception.BusinessException;
import samgoldsee.campus.trading.platform.mapper.CategoryMapper;
import samgoldsee.campus.trading.platform.mapper.ItemMapper;
import samgoldsee.campus.trading.platform.mapper.UserMapper;
import samgoldsee.campus.trading.platform.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 需求/物品服务实现
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Long publish(Long userId, PublishItemReq request) {
        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查分类是否存在
        Category category = categoryMapper.findById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查内容长度
        if (request.getContent().length() > ItemConstant.CONTENT_MAX_LENGTH) {
            throw new BusinessException("描述内容不能超过" + ItemConstant.CONTENT_MAX_LENGTH + "字");
        }

        // TODO: 文本合规校验（接入违规词库）

        // 创建物品实体
        Item item = Item.builder()
                .userId(userId)
                .type(request.getType())
                .categoryId(request.getCategoryId())
                .campus(request.getCampus())
                .price(request.getPrice())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .itemStatus(ItemConstant.STATUS_ACTIVE)
                .createdAt(LocalDateTime.now())
                .expireAt(LocalDateTime.now().plusDays(ItemConstant.EXPIRE_DAYS))
                .build();

        itemMapper.insert(item);
        return item.getId();
    }

    @Override
    public List<ItemResp> getFeedList(Integer type, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        List<Item> items = itemMapper.findFeedList(type, offset, size);
        return convertToResp(items);
    }

    @Override
    public List<ItemResp> search(SearchItemReq request) {
        // 检查关键词长度
        if (request.getKeyword() != null && request.getKeyword().length() > 20) {
            throw new BusinessException("搜索关键词不能超过20字");
        }

        List<Item> items = itemMapper.searchItems(request);
        return convertToResp(items);
    }

    @Override
    public List<ItemResp> getMyItems(Long userId, Integer status, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        List<Item> items = itemMapper.findMyItems(userId, status, offset, size);
        return convertToResp(items);
    }

    @Override
    public ItemResp getDetail(Long id) {
        Item item = itemMapper.findById(id);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        return convertToResp(item);
    }

    @Override
    @Transactional
    public void bump(Long userId, Long itemId) {
        // 检查物品是否存在且属于当前用户
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此物品");
        }
        if (item.getItemStatus() != ItemConstant.STATUS_ACTIVE) {
            throw new BusinessException("只能擦亮展示中的物品");
        }

        int updated = itemMapper.bumpItem(itemId, userId);
        if (updated == 0) {
            throw new BusinessException("擦亮失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public void offline(Long userId, Long itemId) {
        // 检查物品是否存在且属于当前用户
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此物品");
        }

        int updated = itemMapper.offlineItem(itemId, userId);
        if (updated == 0) {
            throw new BusinessException("下架失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public void markSold(Long userId, Long itemId, MarkSoldReq request) {
        // 检查物品是否存在且属于当前用户
        Item item = itemMapper.findById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此物品");
        }
        if (item.getItemStatus() != ItemConstant.STATUS_ACTIVE) {
            throw new BusinessException("只能标记展示中的物品为已成交");
        }

        // 检查成交对象是否存在
        User matchedUser = userMapper.findById(request.getMatchedUserId());
        if (matchedUser == null) {
            throw new BusinessException("成交对象不存在");
        }

        // 标记成交
        int updated = itemMapper.markSold(itemId, userId, request.getMatchedUserId());
        if (updated == 0) {
            throw new BusinessException("标记成交失败，请稍后重试");
        }

        // TODO: 触发信用分增加（成交加分）
    }

    /**
     * 批量过期物品下架（定时任务调用）
     */
    @Transactional
    public int expireItems() {
        return itemMapper.expireItems();
    }

    /**
     * 转换实体到响应DTO
     */
    private List<ItemResp> convertToResp(List<Item> items) {
        return items.stream()
                .map(this::convertToResp)
                .collect(Collectors.toList());
    }

    /**
     * 转换单个实体到响应DTO
     */
    private ItemResp convertToResp(Item item) {
        User user = userMapper.findById(item.getUserId());
        Category category = categoryMapper.findById(item.getCategoryId());

        return ItemResp.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .userNickname(user != null ? user.getNickname() : "未知用户")
                .userCreditScore(user != null ? user.getCreditScore() : 0)
                .type(item.getType())
                .categoryId(item.getCategoryId())
                .categoryName(category != null ? category.getName() : "未分类")
                .campus(item.getCampus())
                .price(item.getPrice())
                .content(item.getContent())
                .imageUrl(item.getImageUrl())
                .itemStatus(item.getItemStatus())
                .createdAt(item.getCreatedAt())
                .expireAt(item.getExpireAt())
                .build();
    }
}