package samgoldsee.campus.trading.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HuangChunXin
 * @date 2026/5/23 09:35
 */
@Getter
@AllArgsConstructor
public enum EmailActionEnum {

	/**
	 * 注册
	 */
	REGISTER("register", "注册"),

	/**
	 * 重置密码
	 */
	RESET_PASSWORD("reset_password", "重置密码");

	/**
	 * 标识码
	 */
	private final String actionCode;

	/**
	 * 角色描述
	 */
	private final String description;

	/**
	 * 根据标识码获取枚举
	 *
	 * @param actionCode 标识码
	 * @return 对应的枚举值，找不到时返回 null
	 */
	public static EmailActionEnum fromCode(String actionCode) {
		if (actionCode == null) {
			return null;
		}
		for (EmailActionEnum emailAction : values()) {
			if (emailAction.getActionCode().equals(actionCode)) {
				return emailAction;
			}
		}
		return null;
	}
}
