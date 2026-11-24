package kr.co.guljima.portal.user;

import java.util.List;
import kr.co.guljima.portal.content.Content;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FavoriteMapper {
    @Select("""
        SELECT c.content_id, c.source_code, c.source_item_id, c.title, c.category,
               c.summary, c.body, c.source_url, c.is_published, c.approved_at, c.updated_at
        FROM favorite f
        JOIN content c ON c.content_id = f.content_id
        WHERE f.user_id = #{userId} AND c.is_published = TRUE
        ORDER BY f.favorited_at DESC
        """)
    List<Content> findForUser(@Param("userId") long userId);

    @Insert("""
        INSERT IGNORE INTO favorite (user_id, content_id)
        SELECT #{userId}, content_id
        FROM content
        WHERE content_id = #{contentId} AND is_published = TRUE
        """)
    int addIfPublished(@Param("userId") long userId, @Param("contentId") long contentId);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND content_id = #{contentId}")
    int remove(@Param("userId") long userId, @Param("contentId") long contentId);
}
