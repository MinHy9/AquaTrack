package com.aquatrack.stats.service;

import com.aquatrack.sensor.repository.WaterQualityLogRepository;
import com.aquatrack.stats.dto.DailySensorStatResponse;
import com.aquatrack.aquarium.repository.AquariumRepository;
import com.aquatrack.aquarium.entity.Aquarium;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final WaterQualityLogRepository logRepository;
    private final AquariumRepository aquariumRepository;

    public List<DailySensorStatResponse> getDailyStatsByBoard(String boardId) {
        Aquarium aquarium = aquariumRepository.findByBoardId(boardId)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 보드 ID입니다: " + boardId));
        return logRepository.getDailyStatsByAquarium(aquarium.getAquariumId());
    }

    public List<DailySensorStatResponse> getWeeklyStatsByBoard(String boardId) {
        Aquarium aquarium = aquariumRepository.findByBoardId(boardId)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 보드 ID입니다: " + boardId));
        return logRepository.getWeeklyStatsByAquarium(aquarium.getAquariumId());
    }

    public List<DailySensorStatResponse> getMonthlyStatsByBoard(String boardId) {
        Aquarium aquarium = aquariumRepository.findByBoardId(boardId)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 보드 ID입니다: " + boardId));
        return logRepository.getMonthlyStatsByAquarium(aquarium.getAquariumId());
    }

    public List<DailySensorStatResponse> getHourlyStatsByBoard(String boardId) {
        Aquarium aquarium = aquariumRepository.findByBoardId(boardId)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 보드 ID입니다: " + boardId));
        LocalDateTime startTime = LocalDateTime.now().minusHours(24);
        return logRepository.getHourlyStatsByAquarium(aquarium.getAquariumId(), startTime);
    }
}
