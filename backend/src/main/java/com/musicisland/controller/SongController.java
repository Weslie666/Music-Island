package com.musicisland.controller;

import com.musicisland.common.Result;
import com.musicisland.entity.Song;
import com.musicisland.mapper.SongMapper;
import com.musicisland.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private SongMapper songMapper;

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre) {
        Map<String, Object> data = songService.getSongs(page, limit, sort, keyword, genre);
        return Result.success(data);
    }

    @GetMapping("/genres")
    public Result<?> genres() {
        List<String> genres = songService.getGenres();
        return Result.success(genres);
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Song song = songService.getSongById(id);
        if (song == null) {
            return Result.error(404, "Song not found");
        }
        return Result.success(song);
    }

    @GetMapping("/{id}/stream")
    public void stream(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        Song song = songService.getSongById(id);
        if (song == null || song.getFileUrl() == null) {
            response.setStatus(404);
            return;
        }

        File audioFile = new File(song.getFileUrl());
        if (!audioFile.exists()) {
            response.setStatus(404);
            return;
        }

        long fileLength = audioFile.length();
        String rangeHeader = request.getHeader("Range");

        songMapper.incrementPlayCount(id);

        try {
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                long start = Long.parseLong(ranges[0]);
                long end = ranges.length > 1 && !ranges[1].isEmpty()
                        ? Long.parseLong(ranges[1])
                        : fileLength - 1;

                if (end >= fileLength) end = fileLength - 1;
                long contentLength = end - start + 1;

                response.setStatus(206);
                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
                response.setContentLengthLong(contentLength);
                response.setContentType("audio/mpeg");

                try (RandomAccessFile raf = new RandomAccessFile(audioFile, "r");
                     OutputStream out = response.getOutputStream()) {
                    raf.seek(start);
                    byte[] buffer = new byte[8192];
                    long remaining = contentLength;
                    int bytesRead;
                    while (remaining > 0 && (bytesRead = raf.read(buffer, 0,
                            (int) Math.min(buffer.length, remaining))) != -1) {
                        out.write(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                }
            } else {
                response.setContentType("audio/mpeg");
                response.setContentLengthLong(fileLength);
                try (FileInputStream fis = new FileInputStream(audioFile);
                     OutputStream out = response.getOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @PostMapping
    public Result<?> add(@RequestBody Song song) {
        Song created = songService.addSong(song);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Song song) {
        song.setId(id);
        Song updated = songService.updateSong(song);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        songService.deleteSong(id);
        return Result.success();
    }
}
