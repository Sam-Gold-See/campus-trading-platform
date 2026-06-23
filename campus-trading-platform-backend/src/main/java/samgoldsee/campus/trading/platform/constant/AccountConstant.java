package samgoldsee.campus.trading.platform.constant;

/**
 * 账号相关常量类
 *
 * @author HuangChunXin
 * @date 2026/5/23 09:40
 */
public class AccountConstant {

	/** 验证码有效时长（分钟） */
	public static final Integer VERIFICATION_CODE_TTL = 5;

	/** 验证码发送间隔（秒） */
	public static final long SEND_CODE_INTERVAL_SECONDS = 60;

	/** 昵称修改冷却期（天） */
	public static final long NICKNAME_COOLDOWN_DAYS = 30;

	/** 新用户初始信用分 */
	public static final int INITIAL_CREDIT_SCORE = 100;

	/** 管理员默认昵称 */
	public static final String ROOT_NICKNAME = "root";

	/** 昵称最小长度 */
	public static final int NICKNAME_MIN_LENGTH = 4;

	/** 昵称最大长度 */
	public static final int NICKNAME_MAX_LENGTH = 16;

	/** 密码最小长度 */
	public static final int PASSWORD_MIN_LENGTH = 8;

	/** 密码最大长度 */
	public static final int PASSWORD_MAX_LENGTH = 20;
}
