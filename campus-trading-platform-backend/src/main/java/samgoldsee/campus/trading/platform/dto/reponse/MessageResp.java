package samgoldsee.campus.trading.platform.dto.reponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息响应DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
@Builder
public class MessageResp {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 发送方ID
     */
    private Long senderId;

    /**
     * 接收方ID
     */
    private Long receiverId;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 发送时间
     */
    private LocalDateTime createdAt;
}