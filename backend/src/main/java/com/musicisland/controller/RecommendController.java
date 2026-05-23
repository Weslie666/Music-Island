package com.musicisland.controller;

import com.musicisland.common.Result;
import com.musicisland.entity.Song;
import com.musicisland.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/popular")
    public Result<?> popular(@RequestParam(defaultValue = "10") int limit) {
        List<Song> songs = recommendService.getPopularRecommendations(limit);
        return Result.success(songs);
    }

    @GetMapping("/personal")
    public Result<?> personal(HttpServletRequest request,
                              @RequestParam(defaultValue = "10") int limit) {
        Long userId = (Long) request.getAttribute("userId");
        List<Song> songs = recommendService.getPersonalRecommendations(userId, limit);
        return Result.success(songs);
    }

    @GetMapping("/similar/{songId}")
    public Result<?> similar(@PathVariable Long songId,
                             @RequestParam(defaultValue = "6") int limit) {
        List<Song> songs = recommendService.getSimilarSongs(songId, limit);
        return Result.success(songs);
    }
}
