package com.musicisland.service;

import java.util.Map;

public interface LikeService {
    Map<String, Object> getMyLikes(Long userId, int page, int limit);
    boolean toggleLike(Long userId, Long songId);
    boolean isLiked(Long userId, Long songId);
}
