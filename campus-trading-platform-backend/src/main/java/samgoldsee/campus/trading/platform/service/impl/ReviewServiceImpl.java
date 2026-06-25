package samgoldsee.campus.trading.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samgoldsee.campus.trading.platform.constant.ItemConstant;
import samgoldsee.campus.trading.platform.dto.request.SubmitReviewReq;
import samgoldsee.campus.trading.platform.dto.reponse.ReviewResp;
import samgoldsee.campus.trading.platform.entity.Item;
import samgoldsee.campus.trading.platform.entity.Review;
import samgoldsee.campus.trading.platform.entity.User;
import samgoldsee.campus.trading.platform.exception.BusinessException;
import samgoldsee.campus.trading.platform.mapper.ItemMapper;
import samgoldsee.campus.trading.platform.mapper.ReviewMapper;
import samgoldsee.campus.trading.platform.mapper.UserMapper;
import samgoldsee.campus.trading.platform.service.ReviewService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评价服务实现
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void submitReview(Long reviewerId, SubmitReviewReq request) {
        // 检查物品是否存在且已成交
        Item item = itemMapper.findById(request.getItemId());
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (item.getItemStatus() != ItemConstant.STATUS_SOLD) {
            throw new BusinessException("只能评价已成交的物品");
        }

        // 检查评价人是否参与了该交易
        boolean isParticipant = item.getUserId().equals(reviewerId) || item.getMatchedUserId().equals(reviewerId);
        if (!isParticipant) {
            throw new BusinessException("您没有权限评价此交易");
        }

        // 检查被评价人是否参与了该交易
        boolean revieweeIsParticipant = item.getUserId().equals(request.getRevieweeId()) || item.getMatchedUserId().equals(request.getRevieweeId());
        if (!revieweeIsParticipant) {
            throw new BusinessException("被评价人未参与此交易");
        }

        // 不能评价自己
        if (reviewerId.equals(request.getRevieweeId())) {
            throw new BusinessException("不能评价自己");
        }

        // 检查是否已评价
        if (reviewMapper.countByItemIdAndReviewerId(request.getItemId(), reviewerId) > 0) {
            throw new BusinessException("您已评价过此交易");
        }

        // 检查被评价人是否存在
        User reviewee = userMapper.findById(request.getRevieweeId());
        if (reviewee == null) {
            throw new BusinessException("被评价人不存在");
        }

        // 创建评价
        Review review = Review.builder()
                .itemId(request.getItemId())
                .reviewerId(reviewerId)
                .revieweeId(request.getRevieweeId())
                .ratingType(request.getRatingType())
                .tags(request.getTags() != null ? String.join(",", request.getTags()) : null)
                .content(request.getContent())
                .build();

        reviewMapper.insert(review);

        // updateCreditScore(request.getRevieweeId(), request.getRatingType());
    }

    @Override
    public List<ReviewResp> getUserReviews(Long revieweeId, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        List<Review> reviews = reviewMapper.findByRevieweeId(revieweeId, offset, size);
        return reviews.stream()
                .map(this::convertToResp)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean hasReviewed(Long itemId, Long reviewerId) {
        return reviewMapper.countByItemIdAndReviewerId(itemId, reviewerId) > 0;
    }

    /**
     * 转换实体到响应DTO
     */
    private ReviewResp convertToResp(Review review) {
        User reviewer = userMapper.findById(review.getReviewerId());
        List<String> tags = review.getTags() != null
                ? Arrays.asList(review.getTags().split(","))
                : Collections.emptyList();

        return ReviewResp.builder()
                .id(review.getId())
                .itemId(review.getItemId())
                .reviewerId(review.getReviewerId())
                .reviewerNickname(reviewer != null ? reviewer.getNickname() : "未知用户")
                .revieweeId(review.getRevieweeId())
                .ratingType(review.getRatingType())
                .tags(tags)
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}