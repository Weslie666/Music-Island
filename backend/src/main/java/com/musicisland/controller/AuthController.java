package com.musicisland.controller;

import com.musicisland.common.Result;
import com.musicisland.dto.LoginRequest;
import com.musicisland.dto.RegisterRequest;
import com.musicisland.entity.User;
import com.musicisland.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterRequest req) {
        User user = userService.register(req.getUsername(), req.getPassword(), req.getNickname());
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        return Result.success(data);
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest req) {
        String token = userService.login(req.getUsername(), req.getPassword());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }

    @GetMapping("/me")
    public Result<?> me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getCurrentUser(userId);
        return Result.success(user);
    }
}
