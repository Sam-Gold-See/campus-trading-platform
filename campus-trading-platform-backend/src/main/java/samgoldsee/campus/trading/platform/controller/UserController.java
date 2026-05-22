package samgoldsee.campus.trading.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import samgoldsee.campus.trading.platform.common.CommonResult;
import samgoldsee.campus.trading.platform.dto.reponse.LoginResp;
import samgoldsee.campus.trading.platform.dto.request.LoginReq;
import samgoldsee.campus.trading.platform.service.UserService;

/**
 * @author HuangChunXin
 * @date 2026/5/22 20:59
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * 用户登录
	 */
	@PostMapping("/login")
	public CommonResult<LoginResp> login(@Valid @RequestBody LoginReq request) {
		LoginResp response = userService.login(request);
		return CommonResult.ok(response);
	}
}
