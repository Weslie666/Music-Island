package com.musicisland.service;

import com.musicisland.entity.Song;

import java.util.List;

public interface RecommendService {
    List<Song> getPopularRecommendations(int limit);
    List<Song> getPersonalRecommendations(Long userId, int limit);
    List<Song> getSimilarSongs(Long songId, int limit);
}
