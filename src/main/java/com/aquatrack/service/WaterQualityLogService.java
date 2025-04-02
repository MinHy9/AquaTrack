package com.aquatrack.service;

import com.aquatrack.dto.WaterQualityLogRequest;
import com.aquatrack.entity.Aquarium;
import com.aquatrack.entity.WaterQualityLog;
import com.aquatrack.repository.AquariumRepository;
import com.aquatrack.repository.WaterQualityLogRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterQualityLogService {
    private final AquariumRepository aquariumRepository;
    private final WaterQualityLogRepository logRepository;
    private final FeedingStateService feedingStateService;

    public WaterQualityLog save(@Valid WaterQualityLogRequest request) {
        Aquarium aquarium = aquariumRepository.findById(request.getAquariumId())
                .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다."));

        WaterQualityLog log = WaterQualityLog.builder()
                .aquarium(aquarium)
                .temperature(request.getTemperature())
                .pH(request.getPH())
                .turbidity(request.getTurbidity())
                .build();

        WaterQualityLog saved = logRepository.save(log);

        // 자동급식 가능 여부 판단
        boolean isNormal = isWaterConditionNormal(saved);
        feedingStateService.setAutoFeedingEnabled(aquarium.getAquariumId(), isNormal);

        return saved;
    }

    public List<WaterQualityLog> getRecentLogs(Long aquariumId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다."));
        return logRepository.findTop10ByAquariumOrderByRecordedAtDesc(aquarium);
    }

    private boolean isWaterConditionNormal(WaterQualityLog log) {
        return log.getTemperature() >= 23.0 && log.getTemperature() <= 28.0 &&
                log.getPH() >= 6.5 && log.getPH() <= 8.0 &&
                log.getTurbidity() <= 300.0;
    }
}
