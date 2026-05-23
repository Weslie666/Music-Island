package com.musicisland.service;

import com.musicisland.entity.Playlist;
import com.musicisland.entity.Song;

import java.util.List;
import java.util.Map;

public interface PlaylistService {
    List<Map<String, Object>> getMyPlaylists(Long userId);
    Map<String, Object> getPlaylistDetail(Long playlistId);
    Playlist createPlaylist(Playlist playlist, Long userId);
    Playlist updatePlaylist(Playlist playlist, Long userId);
    void deletePlaylist(Long playlistId, Long userId);
    void addSong(Long playlistId, Long songId);
    void removeSong(Long playlistId, Long songId);
}
