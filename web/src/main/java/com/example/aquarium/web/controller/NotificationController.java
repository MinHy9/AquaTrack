package com.example.aquarium.web.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquarium.web.entity.Alert;
import com.example.aquarium.web.entity.Notification;
import com.example.aquarium.web.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 📌 유저의 모든 알림 조회
    @GetMapping("/{userid}/notification")
    public List<Notification> findMyNotifications(@PathVariable("userid") Long userid) {
        return notificationService.findNotification(userid);
    }

    // 📌 알림 생성 + 메시지 전송 (sendMessage 호출)
    @PostMapping("/{userid}/notify")
    public ResponseEntity<Void> createAndSendNotifications(@PathVariable("userid") Long userid) {
        notificationService.sendMessage(userid);
        return ResponseEntity.ok().build(); // 200 OK 응답
    }
    @GetMapping("/{userid}/recent-notifications")
    public List<Notification> findRecentNotificationsWithAlert(
            @PathVariable("userid") Long userid,
            @RequestParam("aquariumId") Long aquariumId,
            @RequestParam("cutoff") String cutoffStr) {

        LocalDateTime cutoff = LocalDateTime.parse(cutoffStr); // ISO 8601 형식 날짜 문자열을 LocalDateTime으로 변환
        return notificationService.findRecentNotificationsWithAlertAndAquarium(userid, aquariumId, cutoff);
    }

}
