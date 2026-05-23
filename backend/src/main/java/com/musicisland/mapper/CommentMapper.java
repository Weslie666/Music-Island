package com.musicisland.mapper;

import com.musicisland.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("SELECT c.id, c.user_id, c.song_id, c.content, c.create_time, u.username " +
            "FROM comment c LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.song_id = #{songId} " +
            "ORDER BY c.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Comment> findBySongId(@Param("songId") Long songId,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM comment WHERE song_id = #{songId}")
    long countBySongId(@Param("songId") Long songId);

    @Select("SELECT * FROM comment WHERE id = #{id}")
    Comment findById(@Param("id") Long id);

    @Insert("INSERT INTO comment (user_id, song_id, content) VALUES (#{userId}, #{songId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Delete("DELETE FROM comment WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT c.id, c.user_id, c.song_id, c.content, c.create_time, u.username, s.title as song_title " +
            "FROM comment c LEFT JOIN user u ON c.user_id = u.id LEFT JOIN song s ON c.song_id = s.id " +
            "ORDER BY c.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Comment> findAllWithDetails(@Param("offset") int offset,
                                     @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM comment")
    long countAll();
}
