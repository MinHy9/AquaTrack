package com.aquatrack.controller;

import com.aquatrack.dto.WaterQualityLogRequest;
import com.aquatrack.entity.WaterQualityLog;
import com.aquatrack.service.WaterQualityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/water")
@RequiredArgsConstructor
public class WaterQualityLogController {
    private final WaterQualityLogService waterService;

    // 센서 데이터 저장
    @PostMapping
    public ResponseEntity<WaterQualityLog> save(@RequestBody @Valid WaterQualityLogRequest request) {
        return ResponseEntity.ok(waterService.save(request));
    }

    // 최근 10개 데이터 조회
    @GetMapping("/{aquariumId}")
    public ResponseEntity<List<WaterQualityLog>> getLogs(@PathVariable Long aquariumId) {
        return ResponseEntity.ok(waterService.getRecentLogs(aquariumId));
    }
}
