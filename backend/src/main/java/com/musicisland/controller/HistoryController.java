package com.musicisland.controller;

import com.musicisland.common.Result;
import com.musicisland.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping
    public Result<?> getMyHistory(HttpServletRequest request,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "20") int limit) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> data = historyService.getMyHistory(userId, page, limit);
        return Result.success(data);
    }

    @PostMapping("/{songId}")
    public Result<?> recordPlay(@PathVariable Long songId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        historyService.recordPlay(userId, songId);
        return Result.success();
    }
}
