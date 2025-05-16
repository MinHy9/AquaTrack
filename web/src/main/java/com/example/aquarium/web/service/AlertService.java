package com.example.aquarium.web.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.aquarium.web.entity.Alert;
import com.example.aquarium.web.entity.User;
import com.example.aquarium.web.entity.WaterQualityLog;
import com.example.aquarium.web.repository.AlertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService {
	
	private final AlertRepository alertRepository;
	
	public Alert createAlert(String message, WaterQualityLog log, List<Alert.AlertType> types,User user) {
	    Alert alert = Alert.builder()
	            .waterQualityLog(log)
	            .alertTypes(types)
	            .message(message)
	            .user(user)
	            .resolved(false)
	            .build();   

	    return alertRepository.save(alert);
	}
	
	public List<Alert> getUnresolvedAlerts(Long userId) {
	    return alertRepository.findByUser_UserIdAndResolvedFalse(userId);
	}	
}
