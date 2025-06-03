package com.aquatrack.control.controller;

import com.aquatrack.common.security.CustomUserDetails;
import com.aquatrack.control.dto.DeviceControlRequest;
import com.aquatrack.control.service.DeviceControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<String> controlPump(
            @RequestBody DeviceControlRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userId = String.valueOf(userDetails.getUser().getUserId()); // 또는 getUsername()
        controlService.controlWaterPump(userId, request.getAquariumId(), request.isActivate());
        return ResponseEntity.ok("환수 펌프 제어 완료");
    }

    @PostMapping("/cooler")
    public ResponseEntity<String> controlCooler(
            @RequestBody DeviceControlRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userId = String.valueOf(userDetails.getUser().getUserId()); // 또는 getUsername()
        controlService.controlCoolingFan(userId, request.getAquariumId(), request.isActivate());
        return ResponseEntity.ok("냉각 팬 제어 완료");
    }
}
