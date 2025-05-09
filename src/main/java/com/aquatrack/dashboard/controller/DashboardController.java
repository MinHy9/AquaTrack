package com.aquatrack.dashboard.controller;

import com.aquatrack.dashboard.dto.LatestSensorDataResponse;
import com.aquatrack.sensor.entity.WaterQualityLog;
import com.aquatrack.sensor.repository.WaterQualityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final WaterQualityLogRepository logRepository;

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
}
