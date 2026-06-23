package samgoldsee.campus.trading.platform.constant;

/**
 * Redis 键名前缀常量类
 *
 * @author HuangChunXin
 * @date 2026/6/23
 */
public class RedisConstant {

	/** 注册验证码 */
	public static final String REGISTER_CODE_PREFIX = "register:email:";

	/** 注册验证码发送限流 */
	public static final String REGISTER_RATE_LIMIT_PREFIX = "register:email:rate:";

	/** 昵称修改冷却期 */
	public static final String NICKNAME_COOLDOWN_PREFIX = "nickname:cooldown:";

	/** JWT Token 黑名单 */
	public static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
}
