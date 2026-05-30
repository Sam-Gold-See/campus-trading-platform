package samgoldsee.campus.trading.platform.service;

import jakarta.validation.Valid;
import samgoldsee.campus.trading.platform.dto.reponse.LoginResp;
import samgoldsee.campus.trading.platform.dto.request.LoginReq;
import samgoldsee.campus.trading.platform.dto.request.RegisterReq;
import samgoldsee.campus.trading.platform.dto.request.SendRegisterCodeReq;


/**
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
}
