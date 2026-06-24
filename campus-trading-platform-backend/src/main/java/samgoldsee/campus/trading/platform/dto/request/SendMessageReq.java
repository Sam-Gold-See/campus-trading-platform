package samgoldsee.campus.trading.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发送消息请求DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
public class SendMessageReq {

    /**
     * 接收方ID
     */
    @NotNull(message = "接收方不能为空")
    private Long receiverId;

    /**
     * 物品ID
     */
    @NotNull(message = "物品不能为空")
    private Long itemId;

    /**
     * 消息内容
     */
    @NotNull(message = "消息内容不能为空")
    @Size(min = 1, max = 500, message = "消息长度需在1-500字之间")
    private String content;
}