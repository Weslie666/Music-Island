package com.musicisland.mapper;

import com.musicisland.entity.Song;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserLikeMapper {

    @Select("SELECT s.* FROM song s " +
            "INNER JOIN user_like ul ON s.id = ul.song_id " +
            "WHERE ul.user_id = #{userId} " +
            "ORDER BY ul.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Song> findByUserId(@Param("userId") Long userId,
                            @Param("offset") int offset,
                            @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM user_like WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM user_like WHERE user_id = #{userId} AND song_id = #{songId}")
    int checkLiked(@Param("userId") Long userId, @Param("songId") Long songId);

    @Insert("INSERT INTO user_like (user_id, song_id) VALUES (#{userId}, #{songId})")
    int insert(@Param("userId") Long userId, @Param("songId") Long songId);

    @Delete("DELETE FROM user_like WHERE user_id = #{userId} AND song_id = #{songId}")
    int delete(@Param("userId") Long userId, @Param("songId") Long songId);

    @Select("SELECT COUNT(*) FROM user_like WHERE song_id = #{songId}")
    long countBySongId(@Param("songId") Long songId);

    @Select("SELECT COUNT(*) FROM user_like")
    long countAll();
}
