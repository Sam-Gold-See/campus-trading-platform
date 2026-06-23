package samgoldsee.campus.trading.platform.service;

import samgoldsee.campus.trading.platform.dto.reponse.ItemListResp;

public interface ItemService {

	/** 查询我的发布 */
	ItemListResp getMyItems(Long userId, Integer status, int page, int size);

	/** 提前下架 */
	void offline(Long userId, Long itemId);

	/** 擦亮 */
	void bump(Long userId, Long itemId);
}
