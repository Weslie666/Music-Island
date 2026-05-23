package com.musicisland.service.impl;

import com.musicisland.entity.Song;
import com.musicisland.mapper.SongMapper;
import com.musicisland.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongMapper songMapper;

    @Override
    public Map<String, Object> getSongs(int page, int limit, String sort, String keyword, String genre) {
        int offset = (page - 1) * limit;
        List<Song> records;
        long total;

        if (keyword != null && !keyword.isEmpty()) {
            records = songMapper.search(keyword, offset, limit);
            total = songMapper.countSearch(keyword);
        } else if (genre != null && !genre.isEmpty()) {
            records = songMapper.findByGenre(genre, offset, limit);
            total = songMapper.countByGenre(genre);
        } else if ("latest".equals(sort)) {
            records = songMapper.findLatest(offset, limit);
            total = songMapper.countAll();
        } else {
            records = songMapper.findPopular(offset, limit);
            total = songMapper.countAll();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return result;
    }

    @Override
    public Song getSongById(Long id) {
        return songMapper.findById(id);
    }

    @Override
    public List<String> getGenres() {
        return songMapper.getGenres();
    }

    @Override
    public Song addSong(Song song) {
        songMapper.insert(song);
        return song;
    }

    @Override
    public Song updateSong(Song song) {
        songMapper.update(song);
        return songMapper.findById(song.getId());
    }

    @Override
    public void deleteSong(Long id) {
        songMapper.delete(id);
    }
}
