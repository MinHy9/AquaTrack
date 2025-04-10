package com.example.aquarium.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquarium.web.service.NotificationService;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {
	private NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		super();
		this.notificationService = notificationService;
	}
	
	@PostMapping("/{userid}")
	public ResponseEntity<String> notifyUser(@PathVariable("userid") int userid) {
	    notificationService.createTestAlert(userid);
	    notificationService.sendMessage(userid);
	    return ResponseEntity.ok("테스트 알림 생성 및 문자 발송 완료!");
	}

}
