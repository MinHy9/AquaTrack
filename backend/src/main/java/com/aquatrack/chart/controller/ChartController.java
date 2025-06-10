package com.aquatrack.chart.controller;

import com.aquatrack.chart.dto.ChartDataResponse;
import com.aquatrack.stats.dto.DailySensorStatResponse;
import com.aquatrack.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chart")
@RequiredArgsConstructor
public class ChartController {

    private final StatsService statsService;

    @GetMapping("/{type}")
    public ResponseEntity<List<DailySensorStatResponse>> getChartData(
            @PathVariable String type,
            @RequestParam String boardId) {
        List<DailySensorStatResponse> stats;
        switch (type) {
            case "daily" -> stats = statsService.getDailyStatsByBoard(boardId);
            case "weekly" -> stats = statsService.getWeeklyStatsByBoard(boardId);
            case "monthly" -> stats = statsService.getMonthlyStatsByBoard(boardId);
            case "hourly" -> stats = statsService.getHourlyStatsByBoard(boardId);
            default -> throw new IllegalArgumentException("잘못된 차트 타입입니다: " + type);
        }
        return ResponseEntity.ok(stats);
    }
}

