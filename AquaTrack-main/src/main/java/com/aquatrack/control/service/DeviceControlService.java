package com.aquatrack.control.service;

import com.aquatrack.common.mqtt.MqttService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceControlService {
    private final MqttService mqttService;

    public void controlWaterPump(Long aquariumId, boolean activate) {
        String topic = "aquatrack/" + aquariumId + "/pump";
        mqttService.publish(topic, activate ? "on" : "off");
    }

    public void controlCoolingFan(Long aquariumId, boolean activate) {
        String topic = "aquatrack/" + aquariumId + "/cooler";
        mqttService.publish(topic, activate ? "on" : "off");
    }
}
