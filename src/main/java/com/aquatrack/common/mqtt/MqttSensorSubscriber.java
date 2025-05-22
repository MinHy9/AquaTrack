package com.aquatrack.common.mqtt;

import com.aquatrack.sensor.dto.WaterQualityLogRequest;
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

    @PostConstruct
    public void subscribeToSensorData() {
        try {
            mqttClient.subscribe("aquatrack/+/sensor", (topic, message) -> {
                String payload = new String(message.getPayload());
                log.info("📡 센서 MQTT 수신됨: topic={}, payload={}", topic, payload);

                try {
                    // 1. JSON 파싱 → DTO
                    WaterQualityLogRequest request = objectMapper.readValue(payload, WaterQualityLogRequest.class);

                    // 2. 기존 저장 로직 호출
                    logService.save(request);

                } catch (Exception e) {
                    log.error("❌ 센서 메시지 파싱 실패: {}", e.getMessage());
                }
            });
        } catch (MqttException e) {
            log.error("❌ 센서 토픽 구독 실패: {}", e.getMessage());
        }
    }
}
