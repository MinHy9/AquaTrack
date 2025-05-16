package com.example.aquarium.web.mqtt.config;

import java.util.List;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aquarium.web.entity.WaterQualityLog;
import com.example.aquarium.web.service.WaterQualityLogService;

@Component
public class MqttPublisher {

    private final String brokerUrl = ""; // HiveMQ Cloud 주소
    private final String clientId = "";//server 이름 UUID.randomUUID() 이거 활용해 겹치지 않게 작성 아무렇게 작성해도 상관없음
    private final String topic = "";//본인이 설정한 mqtt topic이름

    private MqttClient client;

    @Autowired
    private final WaterQualityLogService waterQualityLogService;

    @Autowired
    public MqttPublisher(WaterQualityLogService waterQualityLogService) {
        this.waterQualityLogService = waterQualityLogService;
        try {
            client = new MqttClient(brokerUrl, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("");        // HiveMQ Cloud 사용자명
            options.setPassword("".toCharArray());  // HiveMQ Cloud 비밀번호
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //수질 정보가 해당조건에 걸리면 아두이노 쪽에 제어 신호 보냄
    public void sendControlMessage() {
        List<WaterQualityLog> logs = waterQualityLogService.checkWaterQualityLogAndMakeAbnormalLogs(1L);

        if (!logs.isEmpty()) {
            logs.forEach(log -> {
                String controlMessage = generateControlMessage(log);
                this.publish(controlMessage);
            });
        }
    }

    private String generateControlMessage(WaterQualityLog log) {
        if (log.getTemperature() > 28.0) return "FAN_ON";
        if (log.getTemperature() < 23.0) return "HEATER_ON";
        if (log.getTurbidity() > 300.0) return "FILTER_ON";
        if (log.getPh() < 6.5 || log.getPh() > 8.0) return "PH_ADJUST";
        return "NORMAL";
    }
}
