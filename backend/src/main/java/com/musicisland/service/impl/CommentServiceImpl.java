package com.musicisland.service.impl;

import com.musicisland.entity.Comment;
import com.musicisland.mapper.CommentMapper;
import com.musicisland.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Map<String, Object> getCommentsBySongId(Long songId, int page, int limit) {
        int offset = (page - 1) * limit;
        List<Comment> records = commentMapper.findBySongId(songId, offset, limit);
        long total = commentMapper.countBySongId(songId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return result;
    }

    @Override
    public Comment addComment(Long userId, Long songId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setSongId(songId);
        comment.setContent(content);
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的评论");
        }
        commentMapper.delete(commentId);
    }

    @Override
    public Map<String, Object> getAllComments(int page, int limit) {
        int offset = (page - 1) * limit;
        List<Comment> records = commentMapper.findAllWithDetails(offset, limit);
        long total = commentMapper.countAll();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return result;
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        commentMapper.delete(commentId);
    }
}
