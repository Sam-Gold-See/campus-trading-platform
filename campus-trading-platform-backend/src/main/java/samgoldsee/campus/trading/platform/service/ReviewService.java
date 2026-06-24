package samgoldsee.campus.trading.platform.service;

import jakarta.validation.Valid;
import samgoldsee.campus.trading.platform.dto.request.SubmitReviewReq;
import samgoldsee.campus.trading.platform.dto.reponse.ReviewResp;

import java.util.List;

/**
 * 评价服务接口
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
public interface ReviewService {

    /**
     * 提交评价
     */
    void submitReview(Long reviewerId, @Valid SubmitReviewReq request);

    /**
     * 获取用户收到的评价列表
     */
    List<ReviewResp> getUserReviews(Long revieweeId, Integer page, Integer size);

    /**
     * 检查是否已评价
     */
    Boolean hasReviewed(Long itemId, Long reviewerId);
}