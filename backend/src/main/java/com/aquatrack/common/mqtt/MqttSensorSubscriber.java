package com.aquatrack.common.mqtt;

import com.aquatrack.common.websocket.SensorSocketSender;
import com.aquatrack.sensor.dto.WaterQualityLogRequest;
import com.aquatrack.sensor.entity.WaterQualityLog;
import com.aquatrack.sensor.service.WaterQualityLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class MqttSensorSubscriber {

    private final MqttClient mqttClient;
    private final WaterQualityLogService logService;
    private final ObjectMapper objectMapper;
    private final SensorSocketSender sensorSocketSender; // ✅ WebSocket 푸시용

    @PostConstruct
    public void subscribeToSensorData() {
        try {
            mqttClient.subscribe("aquatrack/+/+/sensor", (topic, message) -> {
                String payload = new String(message.getPayload());
                log.info("📡 센서 MQTT 수신됨: topic={}, payload={}", topic, payload);

                try {
                    // 1. topic에서 userId, aquariumId 추출
                    String[] parts = topic.split("/"); // [aquatrack, userId, aquariumId, sensor]
                    String userId = parts[1];
                    Long aquariumId = Long.parseLong(parts[2]);

                    // 2. JSON → DTO 변환
                    WaterQualityLogRequest request = objectMapper.readValue(payload, WaterQualityLogRequest.class);
                    request.setAquariumId(aquariumId);
                    request.setUserId(userId); // DTO에 해당 필드가 있을 경우

                    // 3. 저장 + WebSocket 전송
                    WaterQualityLog savedLog = logService.save(request);
                    sensorSocketSender.send(savedLog);

                } catch (Exception e) {
                    log.error("❌ 센서 메시지 처리 실패: {}", e.getMessage());
                }
            });

            log.info("✅ MQTT 센서 토픽 구독 성공");

        } catch (MqttException e) {
            log.error("❌ 센서 토픽 구독 실패: {}", e.getMessage());
        }
    }
}
