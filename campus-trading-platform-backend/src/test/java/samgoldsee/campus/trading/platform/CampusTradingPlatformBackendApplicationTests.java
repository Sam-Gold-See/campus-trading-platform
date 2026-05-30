package samgoldsee.campus.trading.platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import samgoldsee.campus.trading.platform.dto.request.RegisterReq;
import samgoldsee.campus.trading.platform.dto.request.SendRegisterCodeReq;
import samgoldsee.campus.trading.platform.dto.reponse.LoginResp;
import samgoldsee.campus.trading.platform.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CampusTradingPlatformBackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private UserService userService;

	@Test
	void testSendRegisterCode() {
		SendRegisterCodeReq request = new SendRegisterCodeReq();
		request.setEduEmail("test@m.scnu.edu.cn");

		assertDoesNotThrow(() -> {
			userService.sendRegisterCode(request);
		});
	}

	@Test
	void testRegister() {
		RegisterReq request = new RegisterReq();
		request.setEduEmail("test2026@m.scnu.edu.cn");
		request.setVerificationCode("123456"); // 需要先从Redis获取实际验证码
		request.setNickname("测试用户2026");
		request.setPassword("Test1234");
		request.setConfirmPassword("Test1234");

		LoginResp response = userService.register(request);

		assertNotNull(response);
		assertNotNull(response.getId());
		assertNotNull(response.getToken());
		assertEquals("测试用户2026", response.getNickname());
	}
}
