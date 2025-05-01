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
	 
	@Scheduled(fixedRate = 60000)
    public void sendScheduledNotifications() {
        notificationService.sendMsgsToAllUsersForCaution();
    }
	
//	@Scheduled(fixedRate = 60000)
//	public void sendScheduledReNotifications() {
//		notificationService.resendMsgsToSpecificUsers();
//	}
	
}
