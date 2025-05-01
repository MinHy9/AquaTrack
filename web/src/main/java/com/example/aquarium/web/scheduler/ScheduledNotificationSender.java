package com.example.aquarium.web.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.aquarium.web.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class ScheduledNotificationSender {
	
	private final NotificationService notificationService;
	 
	@Scheduled(fixedRate = 60000)//경고용 메시지 전송 주기 수정 가능
    public void sendScheduledNotifications() {
        notificationService.sendMsgsToAllUsersForCaution();
    }
	
	@Scheduled(fixedRate = 60000)// 메시지 재전송 주기 수정 가능
	public void sendScheduledReNotifications() {
		notificationService.resendMsgsToSpecificUsers();
	}
	
}
