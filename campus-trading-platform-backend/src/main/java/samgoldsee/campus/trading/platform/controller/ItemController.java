package samgoldsee.campus.trading.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import samgoldsee.campus.trading.platform.common.CommonResult;
import samgoldsee.campus.trading.platform.dto.reponse.ItemListResp;
import samgoldsee.campus.trading.platform.service.ItemService;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	@GetMapping("/my-list")
	public CommonResult<ItemListResp> getMyItems(
			@RequestParam(defaultValue = "0") Integer status,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size) {
		String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ItemListResp response = itemService.getMyItems(Long.valueOf(userId), status, page, size);
		return CommonResult.ok(response);
	}

	@PutMapping("/{id}/offline")
	public CommonResult<Void> offline(@PathVariable Long id) {
		String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		itemService.offline(Long.valueOf(userId), id);
		return CommonResult.ok();
	}

	@PutMapping("/{id}/bump")
	public CommonResult<Void> bump(@PathVariable Long id) {
		String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		itemService.bump(Long.valueOf(userId), id);
		return CommonResult.ok();
	}
}
