package samgoldsee.campus.trading.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import samgoldsee.campus.trading.platform.common.CommonResult;
import samgoldsee.campus.trading.platform.dto.request.SubmitReviewReq;
import samgoldsee.campus.trading.platform.dto.reponse.ReviewResp;
import samgoldsee.campus.trading.platform.service.ReviewService;

import java.util.List;

/**
 * 评价Controller
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@RestController
@RequestMapping("/api/evaluate")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 提交评价
     */
    @PostMapping("/submit")
    public CommonResult<Void> submitReview(@Valid @RequestBody SubmitReviewReq request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reviewService.submitReview(Long.valueOf(userId), request);
        return CommonResult.ok();
    }

    /**
     * 获取用户收到的评价列表
     */
    @GetMapping("/user/{revieweeId}")
    public CommonResult<List<ReviewResp>> getUserReviews(
            @PathVariable Long revieweeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ReviewResp> reviews = reviewService.getUserReviews(revieweeId, page, size);
        return CommonResult.ok(reviews);
    }

    /**
     * 检查是否已评价
     */
    @GetMapping("/check")
    public CommonResult<Boolean> hasReviewed(
            @RequestParam Long itemId,
            @RequestParam Long reviewerId) {
        Boolean hasReviewed = reviewService.hasReviewed(itemId, reviewerId);
        return CommonResult.ok(hasReviewed);
    }
}