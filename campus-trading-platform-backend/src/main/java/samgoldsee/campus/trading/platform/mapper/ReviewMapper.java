package samgoldsee.campus.trading.platform.mapper;

import org.apache.ibatis.annotations.*;
import samgoldsee.campus.trading.platform.entity.Review;

import java.util.List;

/**
 * 评价Mapper
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Mapper
public interface ReviewMapper {

    /**
     * 插入评价
     */
    @Insert("""
            INSERT INTO `review` (
                `item_id`,
                `reviewer_id`,
                `reviewee_id`,
                `rating_type`,
                `tags`,
                `content`
            ) VALUES (
                #{itemId},
                #{reviewerId},
                #{revieweeId},
                #{ratingType},
                #{tags},
                #{content}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);

    /**
     * 查询用户收到的评价列表
     */
    @Select("""
            SELECT r.*, u.nickname as reviewer_nickname
            FROM `review` r
            LEFT JOIN `user` u ON r.reviewer_id = u.id
            WHERE r.reviewee_id = #{revieweeId}
            ORDER BY r.created_at DESC
            LIMIT #{offset}, #{size}
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "itemId", column = "item_id"),
            @Result(property = "reviewerId", column = "reviewer_id"),
            @Result(property = "revieweeId", column = "reviewee_id"),
            @Result(property = "ratingType", column = "rating_type"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "content", column = "content"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "reviewerNickname", column = "reviewer_nickname")
    })
    List<Review> findByRevieweeId(@Param("revieweeId") Long revieweeId, @Param("offset") Integer offset, @Param("size") Integer size);

    /**
     * 查询某个物品的评价
     */
    @Select("""
            SELECT * FROM `review`
            WHERE `item_id` = #{itemId}
            """)
    List<Review> findByItemId(@Param("itemId") Long itemId);

    /**
     * 检查用户是否已评价
     */
    @Select("""
            SELECT COUNT(1) FROM `review`
            WHERE `item_id` = #{itemId} AND `reviewer_id` = #{reviewerId}
            """)
    int countByItemIdAndReviewerId(@Param("itemId") Long itemId, @Param("reviewerId") Long reviewerId);

    /**
     * 统计用户的好评数
     */
    @Select("""
            SELECT COUNT(1) FROM `review`
            WHERE `reviewee_id` = #{revieweeId} AND `rating_type` = 1
            """)
    int countGoodReviews(@Param("revieweeId") Long revieweeId);

    /**
     * 统计用户的差评数
     */
    @Select("""
            SELECT COUNT(1) FROM `review`
            WHERE `reviewee_id` = #{revieweeId} AND `rating_type` = -1
            """)
    int countBadReviews(@Param("revieweeId") Long revieweeId);

    /**
     * 统计用户收到的评价总数
     */
    @Select("""
            SELECT COUNT(1) FROM `review`
            WHERE `reviewee_id` = #{revieweeId}
            """)
    int countByRevieweeId(@Param("revieweeId") Long revieweeId);
}