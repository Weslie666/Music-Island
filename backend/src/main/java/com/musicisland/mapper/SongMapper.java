package com.musicisland.mapper;

import com.musicisland.entity.Song;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SongMapper {

    @Select("SELECT * FROM song WHERE genre = #{genre} ORDER BY play_count DESC LIMIT #{offset}, #{limit}")
    List<Song> findByGenre(@Param("genre") String genre, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM song WHERE genre = #{genre}")
    long countByGenre(@Param("genre") String genre);

    @Select("SELECT * FROM song ORDER BY play_count DESC LIMIT #{offset}, #{limit}")
    List<Song> findPopular(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT * FROM song ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Song> findLatest(@Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>"
            + "SELECT * FROM song WHERE 1=1"
            + "<if test='keyword != null and keyword != \"\"'>"
            + " AND (title LIKE CONCAT('%',#{keyword},'%') OR artist LIKE CONCAT('%',#{keyword},'%'))"
            + "</if>"
            + "ORDER BY play_count DESC LIMIT #{offset}, #{limit}"
            + "</script>")
    List<Song> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>"
            + "SELECT COUNT(*) FROM song WHERE 1=1"
            + "<if test='keyword != null and keyword != \"\"'>"
            + " AND (title LIKE CONCAT('%',#{keyword},'%') OR artist LIKE CONCAT('%',#{keyword},'%'))"
            + "</if>"
            + "</script>")
    long countSearch(@Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM song")
    long countAll();

    @Select("SELECT * FROM song WHERE id = #{id}")
    Song findById(@Param("id") Long id);

    @Select("SELECT DISTINCT genre FROM song WHERE genre IS NOT NULL AND genre != ''")
    List<String> getGenres();

    @Select("SELECT genre, COUNT(*) as cnt FROM song WHERE genre IS NOT NULL AND genre != '' GROUP BY genre")
    List<Map<String, Object>> countGroupByGenre();

    @Insert("INSERT INTO song (title, artist, album, genre, duration, cover_url, file_url, play_count, like_count) "
            + "VALUES (#{title}, #{artist}, #{album}, #{genre}, #{duration}, #{coverUrl}, #{fileUrl}, #{playCount}, #{likeCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Song song);

    @Update("UPDATE song SET title=#{title}, artist=#{artist}, album=#{album}, genre=#{genre}, "
            + "duration=#{duration}, cover_url=#{coverUrl}, file_url=#{fileUrl} WHERE id=#{id}")
    int update(Song song);

    @Delete("DELETE FROM song WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Update("UPDATE song SET play_count = play_count + 1 WHERE id = #{id}")
    int incrementPlayCount(@Param("id") Long id);
}
