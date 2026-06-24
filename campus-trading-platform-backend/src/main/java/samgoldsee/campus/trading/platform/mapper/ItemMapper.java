package samgoldsee.campus.trading.platform.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import samgoldsee.campus.trading.platform.dto.request.SearchItemReq;
import samgoldsee.campus.trading.platform.entity.Item;

import java.util.List;

/**
 * 需求/物品Mapper
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Mapper
public interface ItemMapper {

    /**
     * 插入新帖文
     */
    @Insert("""
            INSERT INTO `item` (
                `user_id`,
                `type`,
                `category_id`,
                `campus`,
                `price`,
                `content`,
                `image_url`,
                `item_status`,
                `expire_at`
            ) VALUES (
                #{userId},
                #{type},
                #{categoryId},
                #{campus},
                #{price},
                #{content},
                #{imageUrl},
                #{itemStatus},
                #{expireAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Item item);

    /**
     * 根据ID查询帖文
     */
    @Select("SELECT * FROM `item` WHERE `id` = #{id}")
    Item findById(@Param("id") Long id);

    /**
     * 查询需求墙列表（分页）
     */
    @Select("""
            SELECT i.*, u.nickname as user_nickname, u.credit_score as user_credit_score, c.name as category_name
            FROM `item` i
            LEFT JOIN `user` u ON i.user_id = u.id
            LEFT JOIN `category` c ON i.category_id = c.id
            WHERE i.item_status = 0 AND i.expire_at > NOW()
            AND (#{type} IS NULL OR i.type = #{type})
            ORDER BY i.created_at DESC
            LIMIT #{offset}, #{size}
            """)
    List<Item> findFeedList(@Param("type") Integer type, @Param("offset") Integer offset, @Param("size") Integer size);

    /**
     * 综合搜索（动态SQL）
     */
    @SelectProvider(type = ItemSqlProvider.class, method = "searchItems")
    List<Item> searchItems(SearchItemReq req);

    /**
     * 查询我的发布列表
     */
    @SelectProvider(type = ItemSqlProvider.class, method = "findMyItems")
    List<Item> findMyItems(@Param("userId") Long userId, @Param("status") Integer status, @Param("offset") Integer offset, @Param("size") Integer size);

    /**
     * 擦亮帖文（更新created_at）
     */
    @Update("""
            UPDATE `item`
            SET `created_at` = NOW()
            WHERE `id` = #{id} AND `user_id` = #{userId} AND `item_status` = 0
            """)
    int bumpItem(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 下架帖文
     */
    @Update("""
            UPDATE `item`
            SET `item_status` = 2
            WHERE `id` = #{id} AND `user_id` = #{userId}
            """)
    int offlineItem(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 标记已成交
     */
    @Update("""
            UPDATE `item`
            SET `item_status` = 1, `matched_user_id` = #{matchedUserId}
            WHERE `id` = #{id} AND `user_id` = #{userId} AND `item_status` = 0
            """)
    int markSold(@Param("id") Long id, @Param("userId") Long userId, @Param("matchedUserId") Long matchedUserId);

    /**
     * 批量过期帖文下架
     */
    @Update("""
            UPDATE `item`
            SET `item_status` = 2
            WHERE `item_status` = 0 AND `expire_at` <= NOW()
            """)
    int expireItems();

    /**
     * 统计指定用户的指定状态帖子数量
     */
    @SelectProvider(type = ItemSqlProvider.class, method = "countByUserIdAndStatus")
    int countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 动态SQL提供者类
     */
    public static class ItemSqlProvider {

        public String searchItems(SearchItemReq req) {
            return new SQL() {{
                SELECT("i.*, u.nickname as user_nickname, u.credit_score as user_credit_score, c.name as category_name");
                FROM("`item` i");
                LEFT_OUTER_JOIN("`user` u ON i.user_id = u.id");
                LEFT_OUTER_JOIN("`category` c ON i.category_id = c.id");
                WHERE("i.item_status = 0");
                WHERE("i.expire_at > NOW()");

                if (req.getKeyword() != null && !req.getKeyword().isEmpty()) {
                    WHERE("MATCH(i.content) AGAINST('" + req.getKeyword() + "' IN BOOLEAN MODE)");
                }
                if (req.getType() != null) {
                    WHERE("i.type = " + req.getType());
                }
                if (req.getCategoryId() != null) {
                    WHERE("i.category_id = " + req.getCategoryId());
                }
                if (req.getCampus() != null && !req.getCampus().isEmpty()) {
                    WHERE("i.campus = '" + req.getCampus() + "'");
                }
                if (req.getPriceRange() != null && !req.getPriceRange().isEmpty()) {
                    switch (req.getPriceRange()) {
                        case "below50" -> WHERE("i.price <= 50");
                        case "50to200" -> WHERE("i.price BETWEEN 50 AND 200");
                        case "above200" -> WHERE("i.price > 200");
                        case "negotiable" -> WHERE("i.price IS NULL");
                    }
                }

                if (req.getKeyword() != null && !req.getKeyword().isEmpty()) {
                    ORDER_BY("MATCH(i.content) AGAINST('" + req.getKeyword() + "' IN BOOLEAN MODE) DESC");
                } else {
                    ORDER_BY("i.created_at DESC");
                }

            }}.toString() + " LIMIT " + req.getOffset() + ", " + req.getSize();
        }

        public String findMyItems(@Param("userId") Long userId, @Param("status") Integer status, @Param("offset") Integer offset, @Param("size") Integer size) {
            return new SQL() {{
                SELECT("i.*, u.nickname as user_nickname, u.credit_score as user_credit_score, c.name as category_name");
                FROM("`item` i");
                LEFT_OUTER_JOIN("`user` u ON i.user_id = u.id");
                LEFT_OUTER_JOIN("`category` c ON i.category_id = c.id");
                WHERE("i.user_id = " + userId);
                if (status != null) {
                    WHERE("i.item_status = " + status);
                }
                ORDER_BY("i.created_at DESC");
            }}.toString() + " LIMIT " + offset + ", " + size;
        }

        public String countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status) {
            return new SQL() {{
                SELECT("COUNT(1)");
                FROM("`item`");
                WHERE("user_id = " + userId);
                if (status != null) {
                    WHERE("item_status = " + status);
                }
            }}.toString();
        }
    }
}