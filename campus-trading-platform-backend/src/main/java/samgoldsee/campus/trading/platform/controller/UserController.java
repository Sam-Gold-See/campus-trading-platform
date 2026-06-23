package samgoldsee.campus.trading.platform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import samgoldsee.campus.trading.platform.common.CommonResult;
import samgoldsee.campus.trading.platform.dto.reponse.LoginResp;
import samgoldsee.campus.trading.platform.dto.reponse.ReviewListResp;
import samgoldsee.campus.trading.platform.dto.reponse.UserProfileResp;
import samgoldsee.campus.trading.platform.dto.request.EditNicknameReq;
import samgoldsee.campus.trading.platform.dto.request.EditPasswordReq;
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

	@PutMapping("/editNickname")
	public CommonResult<Void> editNickname(@Valid @RequestBody EditNicknameReq request) {
		String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userService.editNickname(Long.valueOf(userId), request);
		return CommonResult.ok();
	}

	@PutMapping("/editPassword")
	public CommonResult<Void> editPassword(@Valid @RequestBody EditPasswordReq request) {
		String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userService.editPassword(Long.valueOf(userId), request);
		return CommonResult.ok();
	}

	@GetMapping("/profile")
	public CommonResult<UserProfileResp> getProfile() {
		String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserProfileResp response = userService.getProfile(Long.valueOf(userId));
		return CommonResult.ok(response);
	}

	@PostMapping("/logout")
	public CommonResult<Void> logout(HttpServletRequest request) {
		String token = (String) request.getAttribute("Authorization");
		userService.logout(token);
		return CommonResult.ok();
	}

	@GetMapping("/reviews")
	public CommonResult<ReviewListResp> getReviews(
			@RequestParam(required = false) Long targetUserId,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {
		String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long queryUserId = (targetUserId != null) ? targetUserId : Long.valueOf(userId);
		ReviewListResp response = userService.getReviews(queryUserId, page, size);
		return CommonResult.ok(response);
	}
}
