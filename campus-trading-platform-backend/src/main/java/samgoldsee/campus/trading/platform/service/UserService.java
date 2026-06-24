package samgoldsee.campus.trading.platform.service;

import jakarta.validation.Valid;
import samgoldsee.campus.trading.platform.dto.reponse.LoginResp;
import samgoldsee.campus.trading.platform.dto.reponse.UserProfileResp;
import samgoldsee.campus.trading.platform.dto.request.EditNicknameReq;
import samgoldsee.campus.trading.platform.dto.request.EditPasswordReq;
import samgoldsee.campus.trading.platform.dto.request.LoginReq;
import samgoldsee.campus.trading.platform.dto.request.RegisterReq;
import samgoldsee.campus.trading.platform.dto.request.SendRegisterCodeReq;


/**
 * 用户服务接口
 *
 * @author HuangChunXin
 * @date 2026/5/22 21:12
 */
public interface UserService {

    /**
     * 初始化系统管理员用户
     */
    void initAdminUser();

    /**
     * 用户登录
     */
    LoginResp login(@Valid LoginReq request);

    /**
     * 发送注册验证码
     */
    void sendRegisterCode(@Valid SendRegisterCodeReq request);

    /**
     * 用户注册
     */
    LoginResp register(@Valid RegisterReq request);

    /**
     * 修改昵称
     */
    void editNickname(Long userId, @Valid EditNicknameReq request);

    /**
     * 修改密码
     */
    void editPassword(Long userId, @Valid EditPasswordReq request);

    /**
     * 获取用户资料（完整信息）
     */
    UserProfileResp getProfile(Long userId);

    /**
     * 获取用户公开资料（脱敏后）
     */
    UserProfileResp getPublicProfile(Long userId);

    /**
     * 登出
     */
    void logout(String token);
}