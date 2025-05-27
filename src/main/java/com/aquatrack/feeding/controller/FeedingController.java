package com.aquatrack.feeding.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aquatrack.feeding.dto.FeedingScheduleRequest;
import com.aquatrack.feeding.service.FeedingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/feeding")
@RequiredArgsConstructor
public class FeedingController {
    private final FeedingService feedingService;

    // 스케줄 등록 (자동 급여 시간 설정)
    @PostMapping("/schedule")
    public ResponseEntity<?> registerSchedule(@RequestBody FeedingScheduleRequest request) {
        return ResponseEntity.ok(feedingService.registerSchedule(request));
    }

    // 수동 급여 (즉시 먹이 주기)
    @PostMapping("/manual/{aquariumId}")
    public ResponseEntity<?> feedNow(@PathVariable Long aquariumId) {
        boolean result = feedingService.feedNow(aquariumId);
        if (result) return ResponseEntity.ok("급여 성공!");
        else return ResponseEntity.status(HttpStatus.CONFLICT).body("자동 급여 상태가 비활성화되어 수동 급여만 가능");
    }

    // 현재 스케줄 조회
    @GetMapping("/schedule/{aquariumId}")
    public ResponseEntity<Map<String, LocalDateTime>> getSchedules(@PathVariable Long aquariumId) {
        return ResponseEntity.ok(feedingService.getSchedules(aquariumId));
    }
}
