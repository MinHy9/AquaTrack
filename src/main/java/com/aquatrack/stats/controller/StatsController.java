package com.aquatrack.stats.controller;

import com.aquatrack.stats.dto.DailySensorStatResponse;
import com.aquatrack.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/daily")
    public ResponseEntity<List<DailySensorStatResponse>> getDailyStats(Principal principal) {
        return ResponseEntity.ok(statsService.getDailyStats(principal.getName()));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<DailySensorStatResponse>> getWeeklyStats(Principal principal) {
        return ResponseEntity.ok(statsService.getWeeklyStats(principal.getName()));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<DailySensorStatResponse>> getMonthlyStats(Principal principal) {
        return ResponseEntity.ok(statsService.getMonthlyStats(principal.getName()));
    }
}
