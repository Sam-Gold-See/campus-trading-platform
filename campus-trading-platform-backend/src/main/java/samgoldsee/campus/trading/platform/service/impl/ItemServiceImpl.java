package samgoldsee.campus.trading.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import samgoldsee.campus.trading.platform.constant.MessageConstant;
import samgoldsee.campus.trading.platform.dto.reponse.ItemListResp;
import samgoldsee.campus.trading.platform.dto.reponse.ItemResp;
import samgoldsee.campus.trading.platform.exception.BusinessException;
import samgoldsee.campus.trading.platform.mapper.ItemMapper;
import samgoldsee.campus.trading.platform.service.ItemService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemMapper itemMapper;
	private final StringRedisTemplate stringRedisTemplate;

	private static final String BUMP_PREFIX = "bump:";

	@Override
	public ItemListResp getMyItems(Long userId, Integer status, int page, int size) {
		int offset = (page - 1) * size;
		List<ItemResp> list = itemMapper.findByUserIdAndStatus(userId, status, offset, size);
		long total = itemMapper.countByUserIdAndStatus(userId, status);

		return ItemListResp.builder()
				.list(list)
				.total(total)
				.page(page)
				.size(size)
				.build();
	}

	@Override
	public void offline(Long userId, Long itemId) {
		ItemResp item = itemMapper.findById(itemId);
		if (item == null) {
			throw new BusinessException(MessageConstant.ITEM_NOT_FOUND);
		}
		if (item.getItemStatus() != 0) {
			throw new BusinessException(MessageConstant.ITEM_STATUS_INVALID);
		}
		Long ownerId = itemMapper.findUserIdById(itemId);
		if (ownerId == null || !ownerId.equals(userId)) {
			throw new BusinessException(MessageConstant.ITEM_NOT_OWNER);
		}
		itemMapper.offline(itemId, userId);
		log.info("帖文下架成功，itemId: {}, userId: {}", itemId, userId);
	}

	@Override
	public void bump(Long userId, Long itemId) {
		ItemResp item = itemMapper.findById(itemId);
		if (item == null) {
			throw new BusinessException(MessageConstant.ITEM_NOT_FOUND);
		}
		if (item.getItemStatus() != 0) {
			throw new BusinessException(MessageConstant.ITEM_STATUS_INVALID);
		}
		Long ownerId = itemMapper.findUserIdById(itemId);
		if (ownerId == null || !ownerId.equals(userId)) {
			throw new BusinessException(MessageConstant.ITEM_NOT_OWNER);
		}

		// 每日限制：同一帖文每天只能擦亮一次
		String today = LocalDate.now().toString(); // "2026-06-23"
		String bumpKey = BUMP_PREFIX + itemId + ":" + today;
		if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(bumpKey))) {
			throw new BusinessException(MessageConstant.BUMP_LIMIT_EXCEEDED);
		}

		itemMapper.bump(itemId);

		// 过期时间 = 到明天 00:00 的秒数
		long secondsUntilMidnight = Duration.between(
				LocalDateTime.now(),
				LocalDate.now().plusDays(1).atStartOfDay()
		).getSeconds();
		stringRedisTemplate.opsForValue().set(bumpKey, "1", secondsUntilMidnight, TimeUnit.SECONDS);

		log.info("帖文擦亮成功，itemId: {}, userId: {}", itemId, userId);
	}
}
