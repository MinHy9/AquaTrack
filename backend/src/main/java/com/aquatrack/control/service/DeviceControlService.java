package com.aquatrack.control.service;

import com.aquatrack.common.mqtt.MqttService;
import com.aquatrack.aquarium.repository.AquariumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceControlService {
    private final MqttService mqttService;
    private final AquariumRepository aquariumRepository;

    public void controlWaterPump(String userId, Long aquariumId, boolean activate) {
        String boardId = aquariumRepository.findById(aquariumId)
            .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다: " + aquariumId))
            .getBoardId();
        mqttService.publishToDevice(boardId, "pump", activate ? "on" : "off");
    }

    public void controlCoolingFan(String userId, Long aquariumId, boolean activate) {
        String boardId = aquariumRepository.findById(aquariumId)
            .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다: " + aquariumId))
            .getBoardId();
        mqttService.publishToDevice(boardId, "cooler", activate ? "on" : "off");
    }
}
