package kr.co.guljima.portal.user;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("""
        INSERT INTO portal_user (kakao_user_id)
        VALUES (#{kakaoUserId})
        ON DUPLICATE KEY UPDATE kakao_user_id = VALUES(kakao_user_id)
        """)
    int createDemoUserIfMissing(@Param("kakaoUserId") long kakaoUserId);

    @Select("SELECT user_id FROM portal_user WHERE kakao_user_id = #{kakaoUserId}")
    Long findUserIdByKakaoId(@Param("kakaoUserId") long kakaoUserId);
}
