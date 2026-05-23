package com.musicisland.service.impl;

import com.musicisland.entity.Song;
import com.musicisland.mapper.UserBehaviorMapper;
import com.musicisland.mapper.UserLikeMapper;
import com.musicisland.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private UserLikeMapper userLikeMapper;

    @Autowired
    private UserBehaviorMapper behaviorMapper;

    @Override
    public Map<String, Object> getMyLikes(Long userId, int page, int limit) {
        int offset = (page - 1) * limit;
        List<Song> records = userLikeMapper.findByUserId(userId, offset, limit);
        long total = userLikeMapper.countByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return result;
    }

    @Override
    public boolean toggleLike(Long userId, Long songId) {
        boolean liked;
        if (userLikeMapper.checkLiked(userId, songId) > 0) {
            userLikeMapper.delete(userId, songId);
            liked = false;
        } else {
            userLikeMapper.insert(userId, songId);
            liked = true;
        }
        // Also update user_behavior for CF
        behaviorMapper.ensureExists(userId, songId);
        behaviorMapper.updateLikeFlag(userId, songId, liked ? 1 : 0);
        return liked;
    }

    @Override
    public boolean isLiked(Long userId, Long songId) {
        return userLikeMapper.checkLiked(userId, songId) > 0;
    }
}
