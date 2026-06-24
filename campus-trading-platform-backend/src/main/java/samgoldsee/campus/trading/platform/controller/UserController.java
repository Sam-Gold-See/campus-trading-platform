package samgoldsee.campus.trading.platform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import samgoldsee.campus.trading.platform.common.CommonResult;
import samgoldsee.campus.trading.platform.dto.reponse.LoginResp;
import samgoldsee.campus.trading.platform.dto.reponse.UserProfileResp;
import samgoldsee.campus.trading.platform.dto.request.EditNicknameReq;
import samgoldsee.campus.trading.platform.dto.request.EditPasswordReq;
import samgoldsee.campus.trading.platform.dto.request.LoginReq;
import samgoldsee.campus.trading.platform.dto.request.RegisterReq;
import samgoldsee.campus.trading.platform.dto.request.SendRegisterCodeReq;
import samgoldsee.campus.trading.platform.service.UserService;

/**
 * 用户Controller
 *
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

    /**
     * 获取当前用户资料
     */
    @GetMapping("/profile")
    public CommonResult<UserProfileResp> getMyProfile() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileResp response = userService.getProfile(Long.valueOf(userId));
        return CommonResult.ok(response);
    }

    /**
     * 获取指定用户资料（公开信息）
     */
    @GetMapping("/profile/{userId}")
    public CommonResult<UserProfileResp> getUserProfile(@PathVariable Long userId) {
        UserProfileResp response = userService.getPublicProfile(userId);
        return CommonResult.ok(response);
    }

    /**
     * 修改昵称
     */
    @PutMapping("/editNickname")
    public CommonResult<Void> editNickname(@Valid @RequestBody EditNicknameReq request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.editNickname(Long.valueOf(userId), request);
        return CommonResult.ok();
    }

    /**
     * 修改密码
     */
    @PutMapping("/editPassword")
    public CommonResult<Void> editPassword(@Valid @RequestBody EditPasswordReq request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.editPassword(Long.valueOf(userId), request);
        return CommonResult.ok();
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public CommonResult<Void> logout(HttpServletRequest request) {
        String token = (String) request.getAttribute("Authorization");
        userService.logout(token);
        return CommonResult.ok();
    }
}