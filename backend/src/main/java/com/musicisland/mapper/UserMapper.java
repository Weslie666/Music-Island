package com.musicisland.mapper;

import com.musicisland.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Insert("INSERT INTO user (username, password, nickname, role, status) VALUES (#{username}, #{password}, #{nickname}, 'USER', 1)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT * FROM user ORDER BY id ASC LIMIT #{offset}, #{limit}")
    List<User> findAll(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM user")
    long countAll();

    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
