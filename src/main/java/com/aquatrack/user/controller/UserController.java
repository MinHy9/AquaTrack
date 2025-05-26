package com.aquatrack.user.controller;

import com.aquatrack.aquarium.service.AquariumService;
import com.aquatrack.user.entity.User;
import com.aquatrack.aquarium.entity.Aquarium;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AquariumService aquariumService;

    // 로그인된 유저의 첫번째 어항 ID(기본어항) 반환
    @GetMapping("/me")
    public ResponseEntity<?> getMyAquarium(@AuthenticationPrincipal User user) {
        Long aquariumId = aquariumService.getAquariumIdByUser(user.getUserId());
        return ResponseEntity.ok(Map.of("aquariumId", aquariumId));
    }

    // 로그인된 유저의 모든 어항 목록 반환
    @GetMapping("/aquariums")
    public ResponseEntity<?> getMyAquariumList(@AuthenticationPrincipal User user) {
        List<Aquarium> aquariums = aquariumService.getMyAquariums(user.getEmail());

        List<Map<String, Object>> result = aquariums.stream()
                .map(a -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("aquariumId", a.getAquariumId());
                    map.put("name", a.getName());
                    map.put("fishName", a.getFishName());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

}
