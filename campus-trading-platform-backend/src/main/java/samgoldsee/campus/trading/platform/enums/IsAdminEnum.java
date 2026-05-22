package samgoldsee.campus.trading.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 管理员标识枚举
 *
 * @author HuangChunXin
 * @date 2026/5/22 22:00
 */
@Getter
@AllArgsConstructor
public enum IsAdminEnum {

	/**
	 * 普通用户
	 */
	USER(0, "普通用户"),

	/**
	 * 管理员
	 */
	ADMIN(1, "管理员");

	/**
	 * 标识码
	 */
	private final Integer code;

	/**
	 * 角色描述
	 */
	private final String description;

	/**
	 * 根据标识码获取枚举
	 *
	 * @param code 标识码
	 * @return 对应的枚举值，找不到时返回 null
	 */
	public static IsAdminEnum fromCode(Integer code) {
		if (code == null) {
			return null;
		}
		for (IsAdminEnum admin : values()) {
			if (admin.getCode().equals(code)) {
				return admin;
			}
		}
		return null;
	}

	/**
	 * 判断是否为管理员
	 *
	 * @param code 标识码
	 * @return true-管理员，false-非管理员
	 */
	public static boolean isAdmin(Integer code) {
		return ADMIN.getCode().equals(code);
	}

	/**
	 * 判断是否为普通用户
	 *
	 * @param code 标识码
	 * @return true-普通用户，false-非普通用户
	 */
	public static boolean isUser(Integer code) {
		return USER.getCode().equals(code);
	}
}
