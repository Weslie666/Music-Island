package com.musicisland.controller;

import com.musicisland.common.Result;
import com.musicisland.entity.Song;
import com.musicisland.entity.User;
import com.musicisland.mapper.*;
import com.musicisland.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private PlayHistoryMapper playHistoryMapper;

    @Autowired
    private UserLikeMapper userLikeMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    private boolean checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        return "ADMIN".equals(role);
    }

    @GetMapping("/stats")
    public Result<?> getStats(HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return Result.error(403, "需要管理员权限");
        }

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalUsers", userMapper.countAll());
        overview.put("totalSongs", songMapper.countAll());
        overview.put("totalPlays", playHistoryMapper.countAll());
        overview.put("totalLikes", userLikeMapper.countAll());
        overview.put("totalComments", commentMapper.countAll());

        // Genre distribution (GROUP BY, single query)
        List<Map<String, Object>> genreRows = songMapper.countGroupByGenre();
        List<Map<String, Object>> genreData = new java.util.ArrayList<>();
        for (Map<String, Object> row : genreRows) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", row.get("genre"));
            item.put("value", row.get("cnt"));
            genreData.add(item);
        }

        // Top 10 songs by play count
        List<Song> topSongs = songMapper.findPopular(0, 10);

        Map<String, Object> data = new HashMap<>();
        data.put("overview", overview);
        data.put("genreDistribution", genreData);
        data.put("topSongs", topSongs);
        return Result.success(data);
    }

    @GetMapping("/users")
    public Result<?> getUsers(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int limit,
                               HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return Result.error(403, "需要管理员权限");
        }
        int offset = (page - 1) * limit;
        List<User> records = userMapper.findAll(offset, limit);
        long total = userMapper.countAll();

        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", total);
        data.put("page", page);
        data.put("limit", limit);
        return Result.success(data);
    }

    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id,
                                       @RequestBody Map<String, Object> body,
                                       HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return Result.error(403, "需要管理员权限");
        }
        int status = Integer.parseInt(body.get("status").toString());
        userMapper.updateStatus(id, status);
        return Result.success(null);
    }

    @GetMapping("/comments")
    public Result<?> getComments(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int limit,
                                  HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return Result.error(403, "需要管理员权限");
        }
        Map<String, Object> data = commentService.getAllComments(page, limit);
        return Result.success(data);
    }

    @DeleteMapping("/comments/{id}")
    public Result<?> deleteComment(@PathVariable Long id,
                                    HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return Result.error(403, "需要管理员权限");
        }
        commentService.deleteCommentByAdmin(id);
        return Result.success(null);
    }
}
