package com.aquatrack.aquarium.controller;

import com.aquatrack.aquarium.dto.AquariumRequest;
import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.aquarium.service.AquariumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aquariums")
@RequiredArgsConstructor
public class AquariumController {

    private final AquariumService aquariumService;

    // 어항 등록
    @PostMapping
    public ResponseEntity<?> registerAquarium(@RequestBody @Valid AquariumRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Aquarium aquarium = aquariumService.register(email, request);
        return ResponseEntity.ok(aquarium);
    }

    // 내 어항 목록 조회
    @GetMapping
    public ResponseEntity<List<Aquarium>> getMyAquariums() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(aquariumService.getMyAquariums(email));
    }
}
