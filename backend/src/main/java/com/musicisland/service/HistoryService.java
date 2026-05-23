package com.musicisland.service;

import java.util.Map;

public interface HistoryService {
    Map<String, Object> getMyHistory(Long userId, int page, int limit);
    void recordPlay(Long userId, Long songId);
}
