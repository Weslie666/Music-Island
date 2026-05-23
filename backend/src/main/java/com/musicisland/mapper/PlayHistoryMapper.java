package com.musicisland.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PlayHistoryMapper {

    @Select("SELECT s.id, s.title, s.artist, s.album, s.genre, s.duration, " +
            "s.cover_url AS coverUrl, s.file_url AS fileUrl, " +
            "s.play_count AS playCount, s.like_count AS likeCount, " +
            "s.create_time AS createTime, ph.play_time AS playTime " +
            "FROM song s " +
            "INNER JOIN play_history ph ON s.id = ph.song_id " +
            "WHERE ph.user_id = #{userId} " +
            "ORDER BY ph.play_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> findByUserId(@Param("userId") Long userId,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM play_history WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO play_history (user_id, song_id) VALUES (#{userId}, #{songId})")
    int insert(@Param("userId") Long userId, @Param("songId") Long songId);

    @Select("SELECT COUNT(*) FROM play_history")
    long countAll();
}
