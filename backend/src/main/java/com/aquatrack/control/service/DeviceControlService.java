package com.aquatrack.control.service;

import com.aquatrack.common.mqtt.MqttService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceControlService {
    private final MqttService mqttService;

    public void controlWaterPump(String userId, Long aquariumId, boolean activate) {
        mqttService.publishToDevice(userId, aquariumId, "pump", activate ? "on" : "off");
    }

    public void controlCoolingFan(String userId, Long aquariumId, boolean activate) {
        mqttService.publishToDevice(userId, aquariumId, "cooler", activate ? "on" : "off");
    }
}
