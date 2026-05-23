package com.musicisland.controller;

import com.musicisland.common.Constants;
import com.musicisland.common.Result;
import com.musicisland.entity.Comment;
import com.musicisland.security.JwtUtils;
import com.musicisland.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping
    public Result<?> getComments(@RequestParam Long songId,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> data = commentService.getCommentsBySongId(songId, page, limit);
        return Result.success(data);
    }

    @PostMapping
    public Result<?> addComment(@RequestBody Map<String, Object> body,
                                 HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        Long songId = Long.valueOf(body.get("songId").toString());
        String content = (String) body.get("content");
        Comment comment = commentService.addComment(userId, songId, content);
        return Result.success(comment);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteComment(@PathVariable Long id,
                                    HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        String role = getRole(request);
        if ("ADMIN".equals(role)) {
            commentService.deleteCommentByAdmin(id);
        } else {
            commentService.deleteComment(id, userId);
        }
        return Result.success(null);
    }

    private Long getUserId(HttpServletRequest request) {
        // 优先从拦截器设置的属性获取
        Object userId = request.getAttribute("userId");
        if (userId != null) return (Long) userId;
        // 没有被拦截器处理（被排除的路径），手动解析 token
        return parseUserIdFromToken(request);
    }

    private String getRole(HttpServletRequest request) {
        Object role = request.getAttribute("role");
        if (role != null) return (String) role;
        String header = request.getHeader(Constants.TOKEN_HEADER);
        if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
            String token = header.substring(Constants.TOKEN_PREFIX.length());
            if (jwtUtils.validateToken(token)) {
                return (String) jwtUtils.parseToken(token).get("role");
            }
        }
        return null;
    }

    private Long parseUserIdFromToken(HttpServletRequest request) {
        String header = request.getHeader(Constants.TOKEN_HEADER);
        if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
            String token = header.substring(Constants.TOKEN_PREFIX.length());
            if (jwtUtils.validateToken(token)) {
                return jwtUtils.getUserIdFromToken(token);
            }
        }
        return null;
    }
}
