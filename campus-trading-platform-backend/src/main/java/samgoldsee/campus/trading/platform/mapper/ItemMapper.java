package samgoldsee.campus.trading.platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import samgoldsee.campus.trading.platform.dto.reponse.ItemResp;

import java.util.List;

@Mapper
public interface ItemMapper {

	@Select("""
			SELECT i.id, i.type, i.category_id, c.name AS category_name,
			       i.campus, i.price, i.content, i.image_url,
			       i.item_status, i.matched_user_id,
			       i.created_at, i.expire_at
			FROM item i
			JOIN category c ON i.category_id = c.id
			WHERE i.user_id = #{userId}
			  AND i.item_status = #{status}
			ORDER BY i.created_at DESC
			LIMIT #{offset}, #{size}
			""")
	@Results({
		@Result(column = "id", property = "id"),
		@Result(column = "type", property = "type"),
		@Result(column = "category_id", property = "categoryId"),
		@Result(column = "category_name", property = "categoryName"),
		@Result(column = "campus", property = "campus"),
		@Result(column = "price", property = "price"),
		@Result(column = "content", property = "content"),
		@Result(column = "image_url", property = "imageUrl"),
		@Result(column = "item_status", property = "itemStatus"),
		@Result(column = "matched_user_id", property = "matchedUserId"),
		@Result(column = "created_at", property = "createdAt"),
		@Result(column = "expire_at", property = "expireAt"),
	})
	List<ItemResp> findByUserIdAndStatus(@Param("userId") Long userId,
	                                      @Param("status") Integer status,
	                                      @Param("offset") int offset,
	                                      @Param("size") int size);

	@Select("""
			SELECT COUNT(1)
			FROM item
			WHERE user_id = #{userId}
			  AND item_status = #{status}
			""")
	long countByUserIdAndStatus(@Param("userId") Long userId,
	                            @Param("status") Integer status);

	@Select("""
			SELECT i.id, i.type, i.category_id, c.name AS category_name,
			       i.campus, i.price, i.content, i.image_url,
			       i.item_status, i.matched_user_id,
			       i.created_at, i.expire_at
			FROM item i
			JOIN category c ON i.category_id = c.id
			WHERE i.id = #{id}
			""")
	@Results({
		@Result(column = "id", property = "id"),
		@Result(column = "type", property = "type"),
		@Result(column = "category_id", property = "categoryId"),
		@Result(column = "category_name", property = "categoryName"),
		@Result(column = "campus", property = "campus"),
		@Result(column = "price", property = "price"),
		@Result(column = "content", property = "content"),
		@Result(column = "image_url", property = "imageUrl"),
		@Result(column = "item_status", property = "itemStatus"),
		@Result(column = "matched_user_id", property = "matchedUserId"),
		@Result(column = "created_at", property = "createdAt"),
		@Result(column = "expire_at", property = "expireAt"),
	})
	ItemResp findById(@Param("id") Long id);

	@Update("""
			UPDATE item
			SET item_status = #{status}
			WHERE id = #{id}
			""")
	int updateStatus(@Param("id") Long id, @Param("status") Integer status);

	@Update("""
			UPDATE item
			SET created_at = NOW()
			WHERE id = #{id}
			""")
	int bump(@Param("id") Long id);

	@Update("""
			UPDATE item
			SET item_status = 1, matched_user_id = #{matchedUserId}
			WHERE id = #{id}
			""")
	int markSold(@Param("id") Long id, @Param("matchedUserId") Long matchedUserId);
}
