package samgoldsee.campus.trading.platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import samgoldsee.campus.trading.platform.dto.reponse.ReviewResp;

import java.util.List;

@Mapper
public interface ReviewMapper {

	@Select("""
			SELECT r.id, r.item_id AS itemId, r.rating_type AS ratingType,
			       r.tags, r.content, r.created_at AS createdAt,
			       u.nickname AS reviewerNickname
			FROM review r
			JOIN user u ON r.reviewer_id = u.id
			WHERE r.reviewee_id = #{revieweeId}
			ORDER BY r.created_at DESC
			LIMIT #{offset}, #{size}
			""")
	List<ReviewResp> findByRevieweeId(@Param("revieweeId") Long revieweeId,
	                                   @Param("offset") int offset,
	                                   @Param("size") int size);

	@Select("""
			SELECT COUNT(1)
			FROM review
			WHERE reviewee_id = #{revieweeId}
			""")
	long countByRevieweeId(@Param("revieweeId") Long revieweeId);

	@Select("""
			SELECT
				COUNT(CASE WHEN rating_type = 1 THEN 1 END) AS goodCount,
				COUNT(CASE WHEN rating_type = 0 THEN 1 END) AS neutralCount,
				COUNT(CASE WHEN rating_type = -1 THEN 1 END) AS badCount
			FROM review
			WHERE reviewee_id = #{revieweeId}
			""")
	ReviewStats selectStats(@Param("revieweeId") Long revieweeId);

	/** 评价统计内部类 */
	class ReviewStats {
		private Long goodCount;
		private Long neutralCount;
		private Long badCount;

		public Long getGoodCount() { return goodCount; }
		public void setGoodCount(Long goodCount) { this.goodCount = goodCount; }
		public Long getNeutralCount() { return neutralCount; }
		public void setNeutralCount(Long neutralCount) { this.neutralCount = neutralCount; }
		public Long getBadCount() { return badCount; }
		public void setBadCount(Long badCount) { this.badCount = badCount; }
	}
}
