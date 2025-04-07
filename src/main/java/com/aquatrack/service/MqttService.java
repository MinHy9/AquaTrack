package com.aquatrack.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttService {
    private final MqttClient mqttClient;

    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            mqttMessage.setQos(1); // QoS: 0, 1, 2 가능
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            throw new RuntimeException("MQTT 전송 실패: " + e.getMessage());
        }
    }
}
