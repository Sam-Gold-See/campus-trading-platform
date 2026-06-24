package samgoldsee.campus.trading.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import samgoldsee.campus.trading.platform.enums.IsAdminEnum;
import samgoldsee.campus.trading.platform.enums.UserStatusEnum;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 用户核心身份信息与信用分
 *
 * @author HuangChunXin
 * @date 2026/5/22 21:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户唯一标识
     */
    private Long id;

    /**
     * 教育邮箱（实名认证凭证）
     */
    private String eduEmail;

    /**
     * 密码哈希值
     */
    private String passwordHash;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像（采用系统默认或淡化处理的URL）
     */
    private String avatarUrl;

    /**
     * 信用分（初始100）
     */
    private Integer creditScore;

    /**
     * 账号状态（0正常，1禁止拦截发布）
     *
     * @see UserStatusEnum
     */
    private Integer userStatus;

    /**
     * 是否管理员 (0普通用户，1管理员)
     *
     * @see IsAdminEnum
     */
    private Integer isAdmin;

    /**
     * 上次昵称修改时间
     */
    private LocalDateTime lastNicknameChange;

    /**
     * 注册时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}