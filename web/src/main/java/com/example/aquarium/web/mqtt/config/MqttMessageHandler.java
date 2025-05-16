package com.example.aquarium.web.mqtt.config;

import org.json.JSONObject;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import com.example.aquarium.web.service.WaterQualityLogService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MqttMessageHandler implements MessageHandler {
  
	
    private final WaterQualityLogService waterQualityLogService;
    
    @ServiceActivator(inputChannel = "mqttInputChannel")
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String jsonMessage = (String) message.getPayload();

        String topic = (String) message.getHeaders().get("mqtt_receivedTopic"); // ✅ 수정된 부분
        String[] topicParts = topic.split("/");

        Long userId = Long.parseLong(topicParts[2]);
        Long aquariumId = Long.parseLong(topicParts[3]);

        JSONObject json = new JSONObject(jsonMessage);
        float temparature = json.getFloat("temperature");
        float ph = json.getFloat("ph");
        float turbidity = json.getFloat("turbidity");

        handleData(userId, aquariumId, temparature, ph, turbidity);
    }
    
    public void handleData(Long userId, Long aquariumId, float temperature, float ph, float turbidity) {
        waterQualityLogService.saveLog(userId, aquariumId, temperature, ph, turbidity);
    }
} 
