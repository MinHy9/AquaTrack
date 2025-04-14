package com.aquatrack.stats.controller;

import com.aquatrack.stats.dto.DailySensorStatResponse;
import com.aquatrack.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/daily")
    public ResponseEntity<List<DailySensorStatResponse>> getDailyStats() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(statsService.getDailyStats(email));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<DailySensorStatResponse>> getWeeklyStats() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(statsService.getWeeklyStats(email));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<DailySensorStatResponse>> getMonthlyStats() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(statsService.getMonthlyStats(email));
    }
}
