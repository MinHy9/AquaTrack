package com.aquatrack.service;

import com.aquatrack.dto.WaterQualityLogRequest;
import com.aquatrack.entity.Alert;
import com.aquatrack.entity.AlertType;
import com.aquatrack.entity.Aquarium;
import com.aquatrack.entity.WaterQualityLog;
import com.aquatrack.repository.AlertRepository;
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
    private final AlertRepository alertRepository;
    private final NotificationService notificationService;

    public WaterQualityLog save(@Valid WaterQualityLogRequest request) {
        Aquarium aquarium = aquariumRepository.findById(request.getAquariumId())
                .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다."));

        WaterQualityLog log = WaterQualityLog.builder()
                .aquarium(aquarium)
                .temperature(request.getTemperature())
                .pH(request.getPH())
                .turbidity(request.getTurbidity())
                .build();

        WaterQualityLog savedLog = logRepository.save(log);

        // 이상 상태 체크 및 Alert 생성
        checkAndCreateAlerts(savedLog);

        // 자동급식 가능 여부 판단
        boolean isNormal = isWaterConditionNormal(savedLog);
        feedingStateService.setAutoFeedingEnabled(aquarium.getAquariumId(), isNormal);

        return savedLog;
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
    private void checkAndCreateAlerts(WaterQualityLog log) {
        if (log.getTemperature() < 23.0 || log.getTemperature() > 28.0) {
            createAlert(log, AlertType.TEMPERATURE, "수온이 정상 범위를 벗어났습니다.");
        }
        if (log.getPH() < 6.5 || log.getPH() > 8.0) {
            createAlert(log, AlertType.PH, "pH 수치가 정상 범위를 벗어났습니다.");
        }
        if (log.getTurbidity() > 300.0) {
            createAlert(log, AlertType.TURBIDITY, "탁도가 너무 높습니다.");
        }
    }

    private void createAlert(WaterQualityLog log, AlertType type, String message) {
        Alert alert = Alert.builder()
                .log(log)
                .alertType(type)
                .message(message)
                .resolved(false)
                .build();
        Alert saved = alertRepository.save(alert);

        // Alert 생성과 동시에 Notification 전송
        notificationService.sendNotification(saved);
    }
}
