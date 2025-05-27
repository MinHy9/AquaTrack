package com.aquatrack.common.websocket;

import com.aquatrack.sensor.entity.WaterQualityLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SensorSocketSender {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SensorSocketSender(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void send(WaterQualityLog log) {
        messagingTemplate.convertAndSend("/topic/sensor", new SensorDataDTO(log));
    }

    // WebSocket으로 전송할 DTO 정의 (JSON으로 직렬화됨)
    public record SensorDataDTO(float temperature, float ph, float turbidity, String status) {
        public SensorDataDTO(WaterQualityLog log) {
            this(log.getTemperature(), log.getPH(), log.getTurbidity(), log.getStatus());
        }
    }
}
