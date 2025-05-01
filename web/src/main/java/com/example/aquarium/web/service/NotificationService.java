package com.example.aquarium.web.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.aquarium.web.entity.Alert;
import com.example.aquarium.web.entity.Aquarium;
import com.example.aquarium.web.entity.Notification;
import com.example.aquarium.web.entity.User;
import com.example.aquarium.web.entity.WaterQualityLog;
import com.example.aquarium.web.exception.NotificationNotFoundException;
import com.example.aquarium.web.repository.NotificationRespository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final SmsService smsService;
    private final NotificationRespository notificationRepository;
    private final WaterQualityLogService waterQualityLogService;
    private final AlertService alertService;
    private final UserService userService;

    public ResponseEntity<List<Notification>> createWarningMessages(Long userId) {
        List<WaterQualityLog> abnormalLogs = waterQualityLogService.checkWaterQualityLogAndMakeAbnormalLogs(userId);
        User user = userService.findUserById(userId);

        if (abnormalLogs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<Notification> notifications = new ArrayList<>();
        for (WaterQualityLog log : abnormalLogs) {
            String message = waterQualityLogService.generateMessage(log);
            List<Alert.AlertType> types = waterQualityLogService.generateAlertTypes(log);

            Alert newAlert = alertService.createAlert(message, log, types,user);

            Notification notif = Notification.builder()
                .alert(newAlert)
                .message(message)
                .user(user)
                .sentAt(LocalDateTime.now())
                .build();
            Notification savedNotification = notificationRepository.save(notif);       
            notifications.add(savedNotification);
        }
        return ResponseEntity.ok(notifications);
    }
    public boolean isTimeForSendingMessages(User user) {
        List<Notification> notifications = user.getNotifications();
        if (notifications.isEmpty()) return true; // 알림이 하나도 없으면 보내도 됨

        Notification lastNotification = notifications.get(notifications.size() - 1);
        if (lastNotification.getSentAt() == null) return false;

        long minutes = Duration.between(lastNotification.getSentAt(), LocalDateTime.now()).toMinutes();
        return minutes >= 5;
    }

    
    public void sendMsgsToAllUsersForCaution() {
    	List<User> users = userService.findAllUser();
        for (User user : users) {
            if (isTimeForSendingMessages(user)) {
                try {
                    sendMessage(user.getUserId());
                } catch (Exception e) {
                    System.err.println("전송 실패: " + user.getUserId() + " - " + e.getMessage());
                }
            }
        }
    }
    
    public void resendMsgsToSpecificUsers() {
    	List<User> users = userService.findUsersHasUnresolvedAlerts();
    	Predicate<User> predicate = user -> isTimeForSendingMessages(user);
		users.stream()
    		 .filter(predicate)
    		 .forEach(user->{
    			 try {
    				 resendMessage(user.getUserId());
                 } catch (Exception e) {
                     System.err.println("전송 실패: " + user.getUserId() + " - " + e.getMessage());
                 }
    		 });
    }
    
    public ResponseEntity<List<Notification>> createMessageForResending(Long userId) {
        List<Alert> unresolvedAlerts = alertService.getUnresolvedAlerts(userId);
        List<Notification> notificationsToResend = new ArrayList<>();

        for (Alert alert : unresolvedAlerts) {
            // 기존 알림에 대해 Notification을 찾기
            List<Notification> existingNotifications = notificationRepository.findByAlert(alert);

            if (existingNotifications != null && !existingNotifications.isEmpty()) {
                // 예: 가장 최근 Notification만 사용
                Notification latestNotification = existingNotifications
                    .stream()
                    .max(Comparator.comparing(Notification::getSentAt))
                    .orElse(null);

                if (latestNotification != null) {
                	latestNotification.setSentAt(LocalDateTime.now());
                    notificationsToResend.add(latestNotification);
                }
            }
        }
        return ResponseEntity.ok(notificationsToResend);
    }

    public List<Notification> findRecentNotificationsWithAlertAndAquarium(Long userId, Long aquariumId, LocalDateTime cutoff) {
        return notificationRepository.findRecentNotificationsWithAlertAndAquarium(userId, aquariumId, cutoff);
    }

    public void sendMessage(Long userId) {
        User user = userService.findUserById(userId);
        List<Aquarium> aquariums = user.getAquariums();
        ResponseEntity<List<Notification>> response = createWarningMessages(user.getUserId());
        List<Notification> notificationList = response.getBody();

        // 아쿠아리움 목록을 순회하며 알림을 보냄
        aquariums.forEach(aquarium -> {
            if (notificationList != null && !notificationList.isEmpty()) {
                notificationList.forEach(savedNotification -> {
                    Alert alert = savedNotification.getAlert();
                    User recipient = aquarium.getUser();
                    LocalDateTime cutoff = alert.getCreatedAt().minusMinutes(10); // 기준 시간 설정 테스트 할려고 10분으로 함 실시간으로 할려면 1분으로 변경가능

                    // 현재 알림 타입을 Set으로 변환
                    Set<Alert.AlertType> currentTypes = new HashSet<>(alert.getAlertTypes());
                    log.info("현재 알림 타입 목록 (currentTypes):");
                    currentTypes.forEach(type -> log.info(" - {}", type));
                    
                    // 최근 알림을 검색하여 중복 여부를 판단
                    List<Notification> recentNotifications = findRecentNotificationsWithAlertAndAquarium(
                    	    recipient.getUserId(), aquarium.getAquariumId(), cutoff
                    	).stream()
                    	 .filter(n -> !n.getAlert().getAlertId().equals(alert.getAlertId()))  // 자기 자신 제거
                    	 .collect(Collectors.toList());

                    // 최근 알림에서 발송된 알림 타입을 추출하여 Set으로 저장
                    Set<Alert.AlertType> recentSentTypes = recentNotifications.stream()
                            .flatMap(n -> n.getAlert().getAlertTypes().stream())
                            .collect(Collectors.toSet());
                    log.info("최근 알림 타입 목록 (recentSentTypes):");
                    recentSentTypes.forEach(type -> log.info(" - {}", type));

                    // 중복되지 않거나 새로운 타입이 있을 경우 알림 발송
                    boolean shouldSendAlert = Collections.disjoint(currentTypes, recentSentTypes) 
                            || currentTypes.stream().anyMatch(type -> !recentSentTypes.contains(type));

                    // 알림 발송 여부 체크 후, 발송
                    if (shouldSendAlert) {
                        log.info("SMS sent to {}: {}", recipient.getPhoneNumber(), savedNotification.getMessage());
                        smsService.sendSms(recipient.getPhoneNumber(), savedNotification.getMessage());
                    } else {
                        log.info("SMS 생략됨 (중복 경고): {}", savedNotification.getMessage());
                    }
                });
            }
        });
    }
    
    public void resendMessage(Long userId) {
        ResponseEntity<List<Notification>> response = createMessageForResending(userId);
        List<Notification> notificationList = response.getBody();

        if (notificationList != null && !notificationList.isEmpty()) {
            for (Notification savedNotification : notificationList) {
                Alert alert = savedNotification.getAlert();
                Aquarium aquarium = alert.getWaterQualityLog().getAquarium();
                User recipient = aquarium.getUser();
                LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();

                boolean canResend = true;

                for (Alert.AlertType type : alert.getAlertTypes()) {
                    int countToday = notificationRepository.countResendsToday(recipient, aquarium, type, startOfDay);
                    if (countToday >= 3) {
                        log.info("하루 재전송 제한 초과: user={}, aquarium={}, type={}, count={}", recipient.getUserId(), aquarium.getAquariumId(), type, countToday);
                        canResend = false;
                        break;
                    }
                }

                if (canResend) {
                    String resendMessage = "[재전송] " + savedNotification.getMessage(); // 여기!
                    log.info("SMS 재전송 to {}: {}", recipient.getPhoneNumber(), resendMessage);
                    smsService.sendSms(recipient.getPhoneNumber(), resendMessage);
                } else {
                    log.info("SMS 재전송 생략됨 (횟수 초과): {}", savedNotification.getMessage());
                }
            }
        }
    }

    public List<Notification> findNotification(Long id) {
        User user = userService.findUserById(id);
        if (user.getNotifications() != null) {
            return user.getNotifications();
        } else {
            throw new NotificationNotFoundException("Notification 없음");
        }
    }
}

