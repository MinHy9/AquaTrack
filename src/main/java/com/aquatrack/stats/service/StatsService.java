package com.aquatrack.stats.service;

import com.aquatrack.sensor.repository.WaterQualityLogRepository;
import com.aquatrack.stats.dto.DailySensorStatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final WaterQualityLogRepository logRepository;

    public List<DailySensorStatResponse> getDailyStats(String email) {
        return logRepository.getDailyStatsByUser(email);
    }

    public List<DailySensorStatResponse> getWeeklyStats(String email) {
        return logRepository.getWeeklyStats(email);
    }

    public List<DailySensorStatResponse> getMonthlyStats(String email) {
        return logRepository.getMonthlyStats(email);
    }
}
