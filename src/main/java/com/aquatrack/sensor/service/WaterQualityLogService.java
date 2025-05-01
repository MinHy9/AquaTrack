package com.aquatrack.sensor.service;

import com.aquatrack.sensor.dto.WaterQualityLogRequest;
import com.aquatrack.alert.entity.Alert;
import com.aquatrack.alert.entity.AlertType;
import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.sensor.entity.WaterQualityLog;
import com.aquatrack.alert.repository.AlertRepository;
import com.aquatrack.aquarium.repository.AquariumRepository;
import com.aquatrack.sensor.repository.WaterQualityLogRepository;
import com.aquatrack.feeding.service.FeedingStateService;
import com.aquatrack.notification.service.NotificationService;
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

    //사용자기준값에 따라 자동급식 판단
    private boolean isWaterConditionNormal(WaterQualityLog log) {
        Aquarium a = log.getAquarium();

        return log.getTemperature() >= a.getCustomMinTemperature() &&
                log.getTemperature() <= a.getCustomMaxTemperature() &&
                log.getPH() >= a.getCustomMinPH() &&
                log.getPH() <= a.getCustomMaxPH() &&
                log.getTurbidity() <= a.getCustomMaxTurbidity();
    }

    private void checkAndCreateAlerts(WaterQualityLog log) {
        Aquarium a = log.getAquarium();

        if (log.getTemperature() < a.getCustomMinTemperature() || log.getTemperature() > a.getCustomMaxTemperature()) {
            createAlert(log, AlertType.TEMPERATURE, "수온이 정상 범위를 벗어났습니다.");
        }
        if (log.getPH() < a.getCustomMinPH() || log.getPH() > a.getCustomMaxPH()) {
            createAlert(log, AlertType.PH, "pH 수치가 정상 범위를 벗어났습니다.");
        }
        if (log.getTurbidity() > a.getCustomMaxTurbidity()) {
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
