package com.aquatrack.feeding.controller;

import com.aquatrack.common.security.CustomUserDetails;
import com.aquatrack.feeding.dto.FeedingScheduleRequest;
import com.aquatrack.feeding.entity.FeedingSchedule;
import com.aquatrack.feeding.service.FeedingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeding")
@RequiredArgsConstructor
public class FeedingController {
    private final FeedingService feedingService;

    // 스케줄 등록 (자동 급여 시간 설정)
    @PostMapping("/schedule")
    public ResponseEntity<?> registerSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FeedingScheduleRequest request) {
        return ResponseEntity.ok(feedingService.registerSchedule(request, userDetails));
    }
    // 사용자 지정 급여시간 설정
    @PostMapping("/schedules")
    public ResponseEntity<?> registerMultipleSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FeedingScheduleRequest request) {
        feedingService.registerMultipleSchedules(request.getAquariumId(), request.getFeedingTimes(), userDetails);
        return ResponseEntity.ok("다중 급식 시간이 저장되었습니다.");
    }

    // 수동 급여 (즉시 먹이 주기)
    @PostMapping("/manual/{aquariumId}")
    public ResponseEntity<?> feedNow(
            @PathVariable Long aquariumId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userId = String.valueOf(userDetails.getUser().getUserId());
        boolean result = feedingService.feedNow(userId, aquariumId);

        if (result) return ResponseEntity.ok("급여 성공!");
        else return ResponseEntity.status(HttpStatus.CONFLICT).body("자동 급여 상태가 비활성화되어 수동 급여만 가능");
    }


    // 현재 스케줄 조회
    @GetMapping("/schedule/{aquariumId}")
    public ResponseEntity<?> getSchedules(
            @PathVariable Long aquariumId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(feedingService.getSchedules(aquariumId, userDetails));
    }
}
