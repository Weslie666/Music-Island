package com.musicisland.mapper;

import com.musicisland.entity.Song;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlaylistSongMapper {

    @Select("SELECT s.* FROM song s " +
            "INNER JOIN playlist_song ps ON s.id = ps.song_id " +
            "WHERE ps.playlist_id = #{playlistId} " +
            "ORDER BY ps.sort_order ASC, ps.id ASC")
    List<Song> findSongsByPlaylistId(@Param("playlistId") Long playlistId);

    @Insert("INSERT INTO playlist_song (playlist_id, song_id, sort_order) " +
            "VALUES (#{playlistId}, #{songId}, #{sortOrder})")
    int insert(@Param("playlistId") Long playlistId,
               @Param("songId") Long songId,
               @Param("sortOrder") int sortOrder);

    @Delete("DELETE FROM playlist_song WHERE playlist_id = #{playlistId} AND song_id = #{songId}")
    int deleteByPlaylistIdAndSongId(@Param("playlistId") Long playlistId,
                                     @Param("songId") Long songId);

    @Select("SELECT IFNULL(MAX(sort_order), -1) FROM playlist_song WHERE playlist_id = #{playlistId}")
    int getMaxSortOrder(@Param("playlistId") Long playlistId);

    @Select("SELECT COUNT(*) FROM playlist_song WHERE playlist_id = #{playlistId} AND song_id = #{songId}")
    int exists(@Param("playlistId") Long playlistId, @Param("songId") Long songId);

    @Select("SELECT s.cover_url FROM song s " +
            "INNER JOIN playlist_song ps ON s.id = ps.song_id " +
            "WHERE ps.playlist_id = #{playlistId} " +
            "ORDER BY ps.sort_order ASC, ps.id ASC " +
            "LIMIT 4")
    List<String> findFirst4Covers(@Param("playlistId") Long playlistId);
}
