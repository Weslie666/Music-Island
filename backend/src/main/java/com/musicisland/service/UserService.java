package com.musicisland.service;

import com.musicisland.entity.User;

public interface UserService {
    User register(String username, String password, String nickname);
    String login(String username, String password);
    User getCurrentUser(Long userId);
}
