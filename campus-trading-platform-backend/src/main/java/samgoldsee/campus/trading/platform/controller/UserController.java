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
import samgoldsee.campus.trading.platform.dto.request.RegisterReq;
import samgoldsee.campus.trading.platform.dto.request.SendRegisterCodeReq;
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

	/**
	 * 发送注册验证码
	 */
	@PostMapping("/sendRegisterCode")
	public CommonResult<Void> sendRegisterCode(@Valid @RequestBody SendRegisterCodeReq request) {
		userService.sendRegisterCode(request);
		return CommonResult.ok();
	}

	/**
	 * 用户注册
	 */
	@PostMapping("/register")
	public CommonResult<LoginResp> register(@Valid @RequestBody RegisterReq request) {
		LoginResp response = userService.register(request);
		return CommonResult.ok(response);
	}
}
