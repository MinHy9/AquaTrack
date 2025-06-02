package com.aquatrack.aquarium.controller;

import com.aquatrack.aquarium.dto.AquariumRequest;
import com.aquatrack.aquarium.dto.AquariumThresholdUpdateRequest;
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
    public ResponseEntity<?> registerAquarium(@RequestBody @Valid AquariumRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        Aquarium aquarium = aquariumService.register(email, request);
        return ResponseEntity.ok(aquarium);
    }

    // 내 어항 목록 조회
    @GetMapping
    public ResponseEntity<List<Aquarium>> getMyAquariums() {
        // 🔧 여기 수정
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        return ResponseEntity.ok(aquariumService.getMyAquariums(email));
    }

    // 어항 ID로 단일 어항 정보 조회
    @GetMapping("/{aquariumId}")
    public ResponseEntity<Aquarium> getAquarium(@PathVariable Long aquariumId) {
        Aquarium aquarium = aquariumService.getAquariumById(aquariumId);
        return ResponseEntity.ok(aquarium);
    }

    @DeleteMapping("/{aquariumId}")
    public ResponseEntity<Void> deleteAquarium(@PathVariable Long aquariumId) {
        aquariumService.deleteAquarium(aquariumId);
        return ResponseEntity.noContent().build();
    }

    // 온도, 탁도, pH 설정 기준값 설정
    @PutMapping("/{aquariumId}/thresholds")
    public ResponseEntity<String> updateThresholds(@PathVariable Long aquariumId,
                                                   @RequestBody AquariumThresholdUpdateRequest req) {
        aquariumService.updateThresholds(aquariumId, req);
        return ResponseEntity.ok("기준값 수정 완료");
    }
}
