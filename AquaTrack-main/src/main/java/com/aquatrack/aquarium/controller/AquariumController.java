package com.aquatrack.aquarium.controller;

import com.aquatrack.aquarium.dto.AquariumRequest;
import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.aquarium.service.AquariumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aquariums")
@RequiredArgsConstructor
public class AquariumController {

    private final AquariumService aquariumService;

    // 어항 등록
    @PostMapping
    public ResponseEntity<Aquarium> registerAquarium(@RequestBody @Valid AquariumRequest request) {
        String email = getCurrentUserEmail();
        Aquarium aquarium = aquariumService.register(email, request);
        return ResponseEntity.ok(aquarium);
    }

    // 내 어항 목록 조회
    @GetMapping
    public ResponseEntity<List<Aquarium>> getMyAquariums() {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(aquariumService.getMyAquariums(email));
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername(); // 일반적인 Spring Security 인증 객체
        } else if (principal instanceof String string) {
            return string; // @WithMockUser 등에서 제공되는 단순한 username
        } else {
            throw new IllegalStateException("Unexpected authentication principal type: " + principal.getClass());
        }
    }
}


