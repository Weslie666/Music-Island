package com.musicisland.controller;

import com.musicisland.common.Result;
import com.musicisland.entity.Playlist;
import com.musicisland.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @GetMapping
    public Result<?> getMyPlaylists(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Map<String, Object>> playlists = playlistService.getMyPlaylists(userId);
        return Result.success(playlists);
    }

    @GetMapping("/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        Map<String, Object> data = playlistService.getPlaylistDetail(id);
        if (data == null) {
            return Result.error(404, "Playlist not found");
        }
        return Result.success(data);
    }

    @PostMapping
    public Result<?> create(@RequestBody Playlist playlist, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Playlist created = playlistService.createPlaylist(playlist, userId);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Playlist playlist,
                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        playlist.setId(id);
        Playlist updated = playlistService.updatePlaylist(playlist, userId);
        if (updated == null) {
            return Result.error(404, "Playlist not found or permission denied");
        }
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        playlistService.deletePlaylist(id, userId);
        return Result.success();
    }

    @PostMapping("/{id}/songs")
    public Result<?> addSong(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long songId = body.get("songId");
        playlistService.addSong(id, songId);
        return Result.success();
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public Result<?> removeSong(@PathVariable Long id, @PathVariable Long songId) {
        playlistService.removeSong(id, songId);
        return Result.success();
    }
}
