package com.musicisland.mapper;

import com.musicisland.entity.UserBehavior;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserBehaviorMapper {

    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId}")
    List<UserBehavior> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_behavior")
    List<UserBehavior> findAll();

    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} AND song_id = #{songId}")
    UserBehavior findByUserAndSong(@Param("userId") Long userId, @Param("songId") Long songId);

    @Insert("INSERT INTO user_behavior (user_id, song_id, play_count, like_flag) " +
            "VALUES (#{userId}, #{songId}, #{playCount}, #{likeFlag}) " +
            "ON DUPLICATE KEY UPDATE " +
            "play_count = play_count + VALUES(play_count), " +
            "like_flag = CASE WHEN VALUES(like_flag) = 1 THEN 1 ELSE like_flag END, " +
            "update_time = NOW()")
    int upsert(UserBehavior behavior);

    @Update("UPDATE user_behavior SET play_count = play_count + 1, update_time = NOW() " +
            "WHERE user_id = #{userId} AND song_id = #{songId}")
    int incrementPlayCount(@Param("userId") Long userId, @Param("songId") Long songId);

    @Update("UPDATE user_behavior SET like_flag = #{likeFlag}, update_time = NOW() " +
            "WHERE user_id = #{userId} AND song_id = #{songId}")
    int updateLikeFlag(@Param("userId") Long userId, @Param("songId") Long songId,
                       @Param("likeFlag") int likeFlag);

    @Insert("INSERT IGNORE INTO user_behavior (user_id, song_id, play_count, like_flag) " +
            "VALUES (#{userId}, #{songId}, 0, 0)")
    int ensureExists(@Param("userId") Long userId, @Param("songId") Long songId);
}
