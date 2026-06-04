package samgoldsee.campus.trading.platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import samgoldsee.campus.trading.platform.entity.User;

/**
 * @author HuangChunXin
 * @date 2026/5/22 21:25
 */
@Mapper
public interface UserMapper {

	@Insert("""
			<script>
			INSERT INTO `user` (
			   `edu_email`,
			   `password_hash`,
			   `nickname`
			   <if test="avatarUrl != null">, `avatar_url`</if>
			   <if test="creditScore != null">, `credit_score`</if>
			   <if test="userStatus != null">, `user_status`</if>
			   <if test="isAdmin != null">, `is_admin`</if>
			) VALUES (
			   #{eduEmail},
			   #{passwordHash},
			   #{nickname}
			   <if test="avatarUrl != null">, #{avatarUrl}</if>
			   <if test="creditScore != null">, #{creditScore}</if>
			   <if test="userStatus != null">, #{userStatus}</if>
			   <if test="isAdmin != null">, #{isAdmin}</if>
			)
			ON DUPLICATE KEY UPDATE
			   `password_hash` = VALUES(`password_hash`),
			   `nickname` = VALUES(`nickname`)
			   <if test="avatarUrl != null">, `avatar_url` = VALUES(`avatar_url`)</if>
			   <if test="creditScore != null">, `credit_score` = VALUES(`credit_score`)</if>
			   <if test="userStatus != null">, `user_status` = VALUES(`user_status`)</if>
			   <if test="isAdmin != null">, `is_admin` = VALUES(`is_admin`)</if>,
			   `updated_at` = CURRENT_TIMESTAMP
			</script>
			""")
	void insertOrUpdate(User user);

	@Select("""
			select *
	   			from user
	   			where edu_email = #{eduEmail}
			""")
	User findByEduEmail(@Param("eduEmail") String eduEmail);

	@Select("""
			select count(1)
	   			from user
	   			where nickname = #{nickname}
			""")
	int countByNickname(@Param("nickname") String nickname);

	@Select("""
			select *
			from user
			where id = #{id}
			""")
	User findById(@Param("id") Long id);

	@Update("""
			update user
			set nickname = #{nickname}, updated_at = CURRENT_TIMESTAMP
			where id = #{id}
			""")
	int updateNickname(@Param("id") Long id, @Param("nickname") String nickname);
}
