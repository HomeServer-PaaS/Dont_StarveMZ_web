package kr.co.guljima.portal.content;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ContentMapper {
    @Select("""
        SELECT content_id, source_code, source_item_id, title, category, summary, body,
               source_url, is_published, approved_at, updated_at
        FROM content
        WHERE is_published = TRUE
        ORDER BY updated_at DESC, content_id DESC
        """)
    List<Content> findPublished();

    @Select("""
        SELECT content_id, source_code, source_item_id, title, category, summary, body,
               source_url, is_published, approved_at, updated_at
        FROM content
        WHERE content_id = #{contentId} AND is_published = TRUE
        """)
    Content findPublishedById(@Param("contentId") long contentId);
}
