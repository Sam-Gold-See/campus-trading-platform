package samgoldsee.campus.trading.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 初始化会话请求DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
public class InitSessionReq {

    /**
     * 目标用户ID
     */
    @NotNull(message = "目标用户不能为空")
    private Long targetUserId;

    /**
     * 物品ID
     */
    @NotNull(message = "物品不能为空")
    private Long itemId;
}