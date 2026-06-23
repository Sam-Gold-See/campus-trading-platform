package samgoldsee.campus.trading.platform.constant;

/**
 * 信息提示常量类
 *
 * @author HuangChunXin
 * @date 2026/5/23 09:30
 */
public class MessageConstant {

	/** 邮件发送失败 */
	public static final String SEND_EMAIL_FAIL = "发送邮件失败";

	// ==================== 登录相关 ====================
	public static final String USER_NOT_FOUND = "用户不存在";
	public static final String PASSWORD_ERROR = "密码错误";

	// ==================== 注册相关 ====================
	public static final String EMAIL_NOT_EDU = "必须是有效的.edu.cn教育邮箱";
	public static final String EMAIL_ALREADY_REGISTERED = "该邮箱已被注册";
	public static final String CODE_SEND_TOO_FREQUENT = "请勿频繁发送验证码，请60秒后再试";
	public static final String CODE_SEND_FAILED = "验证码发送失败，请稍后重试";
	public static final String CODE_INVALID = "验证码无效或已过期";
	public static final String PASSWORD_NOT_MATCH = "两次输入的密码不一致";
	public static final String NICKNAME_TAKEN = "该昵称已被使用，请更换";

	// ==================== 昵称修改相关 ====================
	public static final String NICKNAME_COOLDOWN = "昵称30天内仅能修改一次，当前仍在冷却期内";

	// ==================== 密码修改相关 ====================
	public static final String NEW_PASSWORD_SAME_AS_OLD = "新密码不能与旧密码相同";
	public static final String NEW_PASSWORD_NOT_MATCH = "两次输入的新密码不一致";
	public static final String OLD_PASSWORD_ERROR = "旧密码输入错误";

	// ==================== Token 黑名单相关 ====================
	public static final String TOKEN_BLACKLISTED = "Token已失效，请重新登录";
}
