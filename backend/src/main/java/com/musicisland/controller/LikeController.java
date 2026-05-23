package com.musicisland.controller;

import com.musicisland.common.Result;
import com.musicisland.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @GetMapping
    public Result<?> getMyLikes(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int limit) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> data = likeService.getMyLikes(userId, page, limit);
        return Result.success(data);
    }

    @PostMapping("/{songId}")
    public Result<?> toggleLike(@PathVariable Long songId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean liked = likeService.toggleLike(userId, songId);
        return Result.success(new HashMap<String, Object>() {{ put("liked", liked); }});
    }

    @GetMapping("/{songId}")
    public Result<?> checkLiked(@PathVariable Long songId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean liked = likeService.isLiked(userId, songId);
        return Result.success(new HashMap<String, Object>() {{ put("liked", liked); }});
    }
}
