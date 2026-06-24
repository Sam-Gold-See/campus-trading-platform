package samgoldsee.campus.trading.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 标记成交请求DTO
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Data
public class MarkSoldReq {

    /**
     * 成交方用户ID
     */
    @NotNull(message = "成交对象不能为空")
    private Long matchedUserId;
}