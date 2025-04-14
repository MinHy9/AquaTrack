package com.aquatrack.notification.service;

import com.aquatrack.alert.entity.Alert;
import com.aquatrack.notification.entity.Notification;
import com.aquatrack.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SmsService smsService;

    public void sendNotification(Alert alert) {
        String phone = alert.getLog().getAquarium().getUser().getPhone();
        if (phone == null) return;

        String message = "[AquaTrack] " + alert.getMessage() + "\n"
                + "확인: https://aquatrack.site/dashboard";

        // SMS 전송
        smsService.sendSms(phone, message);

        // 로그 저장
        Notification notification = Notification.builder()
                .alert(alert)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }
}
