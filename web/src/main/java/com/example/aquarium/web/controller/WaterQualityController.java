package com.example.aquarium.web.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquarium.web.mqtt.config.MqttMessageHandler;
import com.example.aquarium.web.service.AquariumService;
import com.example.aquarium.web.service.WaterQualityLogService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class WaterQualityController {
	
	private final MqttMessageHandler mqttMessageHandler;
	
	//아두이노로 물 에이터(온도,탁도,ph) 받아옴
	@PostMapping("/{userId}/aquariums/{aquariumId}/waterqualitylogs")
	public ResponseEntity<String> receiveWaterQualityData(
	        @PathVariable("userId") Long userId,
	        @PathVariable("aquariumId") Long aquariumId,
	        @RequestBody Map<String, Object> data) {

	    float temperature = Float.parseFloat(data.get("temperature").toString());
	    float ph = Float.parseFloat(data.get("ph").toString());
	    float turbidity = Float.parseFloat(data.get("turbidity").toString());

	    // 처리 로직
	    mqttMessageHandler.handleData(userId, aquariumId, temperature, ph, turbidity);

	    return ResponseEntity.ok("데이터 저장");
	}
		
}
