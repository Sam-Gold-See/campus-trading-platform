package samgoldsee.campus.trading.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户账号状态枚举
 *
 * @author HuangChunXin
 * @date 2026/5/22 21:58
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {

	/**
	 * 正常状态
	 */
	NORMAL(0, "正常"),

	/**
	 * 禁止发布状态
	 */
	BANNED(1, "禁止发布");

	/**
	 * 状态码
	 */
	private final Integer code;

	/**
	 * 状态描述
	 */
	private final String description;

	/**
	 * 根据状态码获取枚举
	 *
	 * @param code 状态码
	 * @return 对应的枚举值，找不到时返回 null
	 */
	public static UserStatusEnum fromCode(Integer code) {
		if (code == null) {
			return null;
		}
		for (UserStatusEnum status : values()) {
			if (status.getCode().equals(code)) {
				return status;
			}
		}
		return null;
	}

	/**
	 * 判断是否为正常状态
	 *
	 * @param code 状态码
	 * @return true-正常，false-非正常
	 */
	public static boolean isNormal(Integer code) {
		return NORMAL.getCode().equals(code);
	}

	/**
	 * 判断是否被禁止发布
	 *
	 * @param code 状态码
	 * @return true-被禁止，false-未被禁止
	 */
	public static boolean isBanned(Integer code) {
		return BANNED.getCode().equals(code);
	}
}
