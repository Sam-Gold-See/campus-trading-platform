package samgoldsee.campus.trading.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import samgoldsee.campus.trading.platform.dto.reponse.ItemListResp;
import samgoldsee.campus.trading.platform.dto.reponse.ItemResp;
import samgoldsee.campus.trading.platform.mapper.ItemMapper;
import samgoldsee.campus.trading.platform.service.ItemService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemMapper itemMapper;

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
}
