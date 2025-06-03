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

        // 1. 상태 판별(status 저장됨)
        boolean isNormal = isWaterConditionNormal(aquarium, request);

        WaterQualityLog log = WaterQualityLog.builder()
                .aquarium(aquarium)
                .temperature(request.getTemperature())
                .pH(request.getPH())
                .turbidity(request.getTurbidity())
                .status(isNormal ? "NORMAL" : "DANGER")
                .build();

        WaterQualityLog savedLog = logRepository.save(log);

        // 2. 이상 상태 체크 및 Alert 생성
        checkAndCreateAlerts(savedLog);

        // 자동급식 가능 여부 판단
        boolean feedingAvailable = isWaterConditionNormal(savedLog);
        feedingStateService.setAutoFeedingEnabled(aquarium.getAquariumId(), feedingAvailable);

        return savedLog;
    }

    public List<WaterQualityLog> getRecentLogs(Long aquariumId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다."));
        return logRepository.findTop10ByAquariumOrderByRecordedAtDesc(aquarium);
    }

    // status 판별용
    private boolean isWaterConditionNormal(Aquarium a, WaterQualityLogRequest request) {
        return request.getTemperature() >= a.getCustomMinTemperature() &&
                request.getTemperature() <= a.getCustomMaxTemperature() &&
                request.getPH() >= a.getCustomMinPH() &&
                request.getPH() <= a.getCustomMaxPH() &&
                request.getTurbidity() <= a.getCustomMaxTurbidity();
    }

    // 급식 판단용
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
            double temp = log.getTemperature();
            String msg = String.format("현재 수온은 %.1f°C로 정상 범위를 벗어났습니다.", temp);
            createAlert(log, AlertType.TEMPERATURE, msg);

        }
        if (log.getPH() < a.getCustomMinPH() || log.getPH() > a.getCustomMaxPH()) {
            double ph = log.getPH();
            String msg = String.format("현재 pH는 %.1f로 정상 범위를 벗어났습니다.", ph);
            createAlert(log, AlertType.PH, msg);

        }
        if (log.getTurbidity() > a.getCustomMaxTurbidity()) {
            double turb = log.getTurbidity();
            String msg = String.format("현재 탁도는 %.1f NTU로 정상 범위를 벗어났습니다.", turb);
            createAlert(log, AlertType.TURBIDITY, msg);
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
