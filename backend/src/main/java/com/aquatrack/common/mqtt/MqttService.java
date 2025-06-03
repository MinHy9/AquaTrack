package com.aquatrack.common.mqtt;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttService {
    private final MqttClient mqttClient;

    //메시지 publish 담당
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

    // boardId 기반으로 토픽 발행
    public void publishToDevice(String boardId, String device, String message) {
        String topic = "aquatrack/" + boardId + "/" + device;
        publish(topic, message);
    }
}
