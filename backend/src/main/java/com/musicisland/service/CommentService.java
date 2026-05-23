package com.musicisland.service;

import com.musicisland.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {
    Map<String, Object> getCommentsBySongId(Long songId, int page, int limit);
    Comment addComment(Long userId, Long songId, String content);
    void deleteComment(Long commentId, Long userId);
    Map<String, Object> getAllComments(int page, int limit);
    void deleteCommentByAdmin(Long commentId);
}
