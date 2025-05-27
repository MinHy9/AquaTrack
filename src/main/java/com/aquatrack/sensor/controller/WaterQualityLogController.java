/*package com.aquatrack.sensor.controller;

import com.aquatrack.sensor.dto.WaterQualityLogRequest;
import com.aquatrack.sensor.entity.WaterQualityLog;
import com.aquatrack.sensor.service.WaterQualityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/water")
@RequiredArgsConstructor
public class WaterQualityLogController {
    private final WaterQualityLogService waterService;

    // 센서 데이터 저장
    @PostMapping
    public ResponseEntity<WaterQualityLog> save(@RequestBody @Valid WaterQualityLogRequest request) {
    	request.setRecoredAt(LocalDateTime.now());
    	waterService.sendAll(request);
        return ResponseEntity.ok(waterService.save(request));
    }

    // 최근 10개 데이터 조회
    @GetMapping("/{aquariumId}")
    public ResponseEntity<List<WaterQualityLog>> getLogs(@PathVariable Long aquariumId) {
        return ResponseEntity.ok(waterService.getRecentLogs(aquariumId));
    }
}*/

//http post 방식에서 mqtt방식으로 전환했으므로 주석처리함
