package com.musicisland.service.impl;

import com.musicisland.entity.User;
import com.musicisland.mapper.UserMapper;
import com.musicisland.security.JwtUtils;
import com.musicisland.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public User register(String username, String password, String nickname) {
        User exist = userMapper.findByUsername(username);
        if (exist != null) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setNickname(nickname != null ? nickname : username);
        userMapper.insert(user);
        return user;
    }

    @Override
    public String login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("Account is frozen");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }
        return jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public User getCurrentUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setPassword(null); // Never expose password
        }
        return user;
    }
}
