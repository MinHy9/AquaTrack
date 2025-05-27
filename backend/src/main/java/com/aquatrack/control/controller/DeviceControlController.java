package com.aquatrack.control.controller;

import com.aquatrack.control.dto.DeviceControlRequest;
import com.aquatrack.control.service.DeviceControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control")
@RequiredArgsConstructor
public class DeviceControlController {
    private final DeviceControlService controlService;

    @PostMapping("/pump")
    public ResponseEntity<String> controlPump(@RequestBody DeviceControlRequest request) {
        controlService.controlWaterPump(request.getAquariumId(), request.isActivate());
        return ResponseEntity.ok("환수 펌프 제어 완료");
    }

    @PostMapping("/cooler")
    public ResponseEntity<String> controlCooler(@RequestBody DeviceControlRequest request) {
        controlService.controlCoolingFan(request.getAquariumId(), request.isActivate());
        return ResponseEntity.ok("냉각 팬 제어 완료");
    }
}
