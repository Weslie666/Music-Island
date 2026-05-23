package com.musicisland.service;

import com.musicisland.entity.Song;

import java.util.List;
import java.util.Map;

public interface SongService {
    Map<String, Object> getSongs(int page, int limit, String sort, String keyword, String genre);
    Song getSongById(Long id);
    List<String> getGenres();
    Song addSong(Song song);
    Song updateSong(Song song);
    void deleteSong(Long id);
}
