package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话响应DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
@Builder
public class ConversationResp {

    /**
     * 对方用户ID
     */
    private Long peerId;

    /**
     * 对方昵称
     */
    private String peerNickname;

    /**
     * 对方头像
     */
    private String peerAvatar;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 物品标题（内容前20字）
     */
    private String itemTitle;

    /**
     * 最后一条消息
     */
    private String lastMessage;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastTime;

    /**
     * 未读消息数
     */
    private Integer unreadCount;
}