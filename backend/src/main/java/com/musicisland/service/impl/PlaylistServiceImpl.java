package com.musicisland.service.impl;

import com.musicisland.entity.Playlist;
import com.musicisland.entity.Song;
import com.musicisland.mapper.PlaylistMapper;
import com.musicisland.mapper.PlaylistSongMapper;
import com.musicisland.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistMapper playlistMapper;

    @Autowired
    private PlaylistSongMapper playlistSongMapper;

    @Override
    public List<Map<String, Object>> getMyPlaylists(Long userId) {
        List<Playlist> playlists = playlistMapper.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Playlist pl : playlists) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", pl.getId());
            item.put("name", pl.getName());
            item.put("description", pl.getDescription());
            item.put("userId", pl.getUserId());
            item.put("isPublic", pl.getIsPublic());
            item.put("createTime", pl.getCreateTime());
            item.put("covers", playlistSongMapper.findFirst4Covers(pl.getId()));
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> getPlaylistDetail(Long playlistId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null) return null;

        List<Song> songs = playlistSongMapper.findSongsByPlaylistId(playlistId);

        Map<String, Object> result = new HashMap<>();
        result.put("playlist", playlist);
        result.put("songs", songs);
        return result;
    }

    @Override
    public Playlist createPlaylist(Playlist playlist, Long userId) {
        playlist.setUserId(userId);
        playlist.setIsPublic(playlist.getIsPublic() != null ? playlist.getIsPublic() : 1);
        playlistMapper.insert(playlist);
        return playlist;
    }

    @Override
    public Playlist updatePlaylist(Playlist playlist, Long userId) {
        Playlist existing = playlistMapper.findById(playlist.getId());
        if (existing == null || !existing.getUserId().equals(userId)) return null;
        playlistMapper.update(playlist);
        return playlistMapper.findById(playlist.getId());
    }

    @Override
    @Transactional
    public void deletePlaylist(Long playlistId, Long userId) {
        Playlist existing = playlistMapper.findById(playlistId);
        if (existing == null || !existing.getUserId().equals(userId)) return;
        playlistMapper.delete(playlistId);
    }

    @Override
    public void addSong(Long playlistId, Long songId) {
        if (playlistSongMapper.exists(playlistId, songId) > 0) return;
        int maxOrder = playlistSongMapper.getMaxSortOrder(playlistId);
        playlistSongMapper.insert(playlistId, songId, maxOrder + 1);
    }

    @Override
    public void removeSong(Long playlistId, Long songId) {
        playlistSongMapper.deleteByPlaylistIdAndSongId(playlistId, songId);
    }
}
