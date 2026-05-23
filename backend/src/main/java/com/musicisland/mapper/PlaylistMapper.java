package com.musicisland.mapper;

import com.musicisland.entity.Playlist;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlaylistMapper {

    @Select("SELECT * FROM playlist WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Playlist> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM playlist WHERE id = #{id}")
    Playlist findById(@Param("id") Long id);

    @Insert("INSERT INTO playlist (name, description, cover_url, user_id, is_public) " +
            "VALUES (#{name}, #{description}, #{coverUrl}, #{userId}, #{isPublic})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Playlist playlist);

    @Update("UPDATE playlist SET name=#{name}, description=#{description}, " +
            "cover_url=#{coverUrl}, is_public=#{isPublic} WHERE id=#{id}")
    int update(Playlist playlist);

    @Delete("DELETE FROM playlist WHERE id = #{id}")
    int delete(@Param("id") Long id);
}
