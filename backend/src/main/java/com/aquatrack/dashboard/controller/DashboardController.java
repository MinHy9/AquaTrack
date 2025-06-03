package com.aquatrack.dashboard.controller;

import com.aquatrack.alert.entity.Alert;
import com.aquatrack.alert.repository.AlertRepository;
import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.aquarium.repository.AquariumRepository;
import com.aquatrack.dashboard.dto.*;
import com.aquatrack.sensor.entity.WaterQualityLog;
import com.aquatrack.sensor.repository.WaterQualityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final WaterQualityLogRepository logRepository;
    private final AlertRepository alertRepository;
    private final AquariumRepository aquariumRepository;

    @GetMapping("/data")
    public ResponseEntity<LatestSensorDataResponse> getLatestSensorData(Principal principal) {
        String email = principal.getName();

        WaterQualityLog latestLog = logRepository.findTopByAquarium_User_EmailOrderByRecordedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("센서 데이터가 없습니다."));

        return ResponseEntity.ok(LatestSensorDataResponse.builder()
                .temperature(latestLog.getTemperature())
                .pH(latestLog.getPH())
                .turbidity(latestLog.getTurbidity())
                .recordedAt(latestLog.getRecordedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())
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
                .recordedAt(log.getRecordedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())
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

    @GetMapping("/aquarium/thresholds")
    public ResponseEntity<AquariumThresholdResponse> getThresholds(Principal principal) {
        String email = principal.getName();

        Aquarium aquarium = aquariumRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다."));

        return ResponseEntity.ok(AquariumThresholdResponse.builder()
                .fishName(aquarium.getFishName())
                .minTemperature(aquarium.getCustomMinTemperature())
                .maxTemperature(aquarium.getCustomMaxTemperature())
                .minPH(aquarium.getCustomMinPH())
                .maxPH(aquarium.getCustomMaxPH())
                .maxTurbidity(aquarium.getCustomMaxTurbidity())
                .build());
    }

    @PutMapping("/aquarium/thresholds")
    public ResponseEntity<String> updateThresholds(@RequestBody AquariumThresholdUpdateRequest req,
                                                   Principal principal) {
        String email = principal.getName();

        Aquarium aquarium = aquariumRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다."));

        aquarium.setCustomMinTemperature(req.getMinTemperature());
        aquarium.setCustomMaxTemperature(req.getMaxTemperature());
        aquarium.setCustomMinPH(req.getMinPH());
        aquarium.setCustomMaxPH(req.getMaxPH());
        aquarium.setCustomMaxTurbidity(req.getMaxTurbidity());

        aquariumRepository.save(aquarium);

        return ResponseEntity.ok("기준값이 성공적으로 수정되었습니다.");
    }
}
