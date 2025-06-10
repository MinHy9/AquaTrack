package com.aquatrack.stats.controller;

import com.aquatrack.stats.dto.DailySensorStatResponse;
import com.aquatrack.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/daily")
    public ResponseEntity<List<DailySensorStatResponse>> getDailyStats(@RequestParam String boardId) {
        return ResponseEntity.ok(statsService.getDailyStatsByBoard(boardId));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<DailySensorStatResponse>> getWeeklyStats(@RequestParam String boardId) {
        return ResponseEntity.ok(statsService.getWeeklyStatsByBoard(boardId));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<DailySensorStatResponse>> getMonthlyStats(@RequestParam String boardId) {
        return ResponseEntity.ok(statsService.getMonthlyStatsByBoard(boardId));
    }
}
