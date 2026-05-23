package com.musicisland.service.impl;

import com.musicisland.mapper.PlayHistoryMapper;
import com.musicisland.mapper.UserBehaviorMapper;
import com.musicisland.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private PlayHistoryMapper playHistoryMapper;

    @Autowired
    private UserBehaviorMapper behaviorMapper;

    @Override
    public Map<String, Object> getMyHistory(Long userId, int page, int limit) {
        int offset = (page - 1) * limit;
        List<Map<String, Object>> records = playHistoryMapper.findByUserId(userId, offset, limit);
        long total = playHistoryMapper.countByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return result;
    }

    @Override
    public void recordPlay(Long userId, Long songId) {
        playHistoryMapper.insert(userId, songId);
        // Also update user_behavior for CF
        behaviorMapper.ensureExists(userId, songId);
        behaviorMapper.incrementPlayCount(userId, songId);
    }
}
