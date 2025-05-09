package com.aquatrack.dashboard.controller;

import com.aquatrack.alert.entity.Alert;
import com.aquatrack.alert.repository.AlertRepository;
import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.dashboard.dto.AlertHistoryResponse;
import com.aquatrack.dashboard.dto.AquariumStatusResponse;
import com.aquatrack.dashboard.dto.LatestSensorDataResponse;
import com.aquatrack.sensor.entity.WaterQualityLog;
import com.aquatrack.sensor.repository.WaterQualityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final WaterQualityLogRepository logRepository;
    private final AlertRepository alertRepository;

    @GetMapping("/data")
    public ResponseEntity<LatestSensorDataResponse> getLatestSensorData(Principal principal) {
        String email = principal.getName();

        WaterQualityLog latestLog = logRepository.findTopByAquarium_User_EmailOrderByRecordedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("센서 데이터가 없습니다."));

        return ResponseEntity.ok(LatestSensorDataResponse.builder()
                .temperature(latestLog.getTemperature())
                .pH(latestLog.getPH())
                .turbidity(latestLog.getTurbidity())
                .recordedAt(latestLog.getRecordedAt())
                .build());
    }

    @GetMapping("/aquarium/status")
    public ResponseEntity<AquariumStatusResponse> getAquariumStatus(Principal principal) {
        String email = principal.getName();

        WaterQualityLog log = logRepository.findTopByAquarium_User_EmailOrderByRecordedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("센서 데이터가 없습니다."));

        Aquarium aquarium = log.getAquarium();

        return ResponseEntity.ok(AquariumStatusResponse.builder()
                .fishName(aquarium.getFishName())
                .temperature(log.getTemperature())
                .temperatureStatus(getStatus(log.getTemperature(),
                        aquarium.getCustomMinTemperature(),
                        aquarium.getCustomMaxTemperature()))
                .pH(log.getPH())
                .pHStatus(getStatus(log.getPH(),
                        aquarium.getCustomMinPH(),
                        aquarium.getCustomMaxPH()))
                .turbidity(log.getTurbidity())
                .turbidityStatus(log.getTurbidity() > aquarium.getCustomMaxTurbidity() ? "경고" : "정상")
                .recordedAt(log.getRecordedAt())
                .build());
    }

    private String getStatus(Float value, Float min, Float max) {
        if (value == null || min == null || max == null) return "데이터 없음";
        return (value < min || value > max) ? "경고" : "정상";
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<AlertHistoryResponse>> getAlertHistory(Principal principal) {
        String email = principal.getName();

        List<Alert> alerts = alertRepository.findAllByUserEmail(email);

        List<AlertHistoryResponse> responses = alerts.stream()
                .map(a -> AlertHistoryResponse.builder()
                        .alertType(a.getAlertType().name())
                        .message(a.getMessage())
                        .createdAt(a.getCreatedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(responses);
    }


}
