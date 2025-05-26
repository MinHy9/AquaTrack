package com.aquatrack.chart.controller;

import com.aquatrack.chart.dto.ChartDataResponse;
import com.aquatrack.stats.dto.DailySensorStatResponse;
import com.aquatrack.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chart")
@RequiredArgsConstructor
public class ChartController {

    private final StatsService statsService;

    @GetMapping("/{range}") // range = daily, weekly, monthly
    public ResponseEntity<ChartDataResponse> getChart(@PathVariable String range, Principal principal) {
        String email = principal.getName();
        List<DailySensorStatResponse> stats;

        switch (range) {
            case "weekly" -> stats = statsService.getWeeklyStats(email);
            case "monthly" -> stats = statsService.getMonthlyStats(email);
            default -> stats = statsService.getDailyStats(email);
        }

        ChartDataResponse response = ChartDataResponse.builder()
                .categories(stats.stream().map(s -> s.getDateLabel().toString()).toList())
                .temperature(stats.stream().map(DailySensorStatResponse::getTemperatureAvg).toList())
                .ph(stats.stream().map(DailySensorStatResponse::getPHAvg).toList())
                .turbidity(stats.stream().map(DailySensorStatResponse::getTurbidityAvg).toList())
                .build();

        return ResponseEntity.ok(response);
    }
}

