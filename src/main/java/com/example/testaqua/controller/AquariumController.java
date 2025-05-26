package com.example.testaqua.controller;

import com.example.testaqua.model.Aquarium;
import com.example.testaqua.service.AquariumService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aquarium")
public class AquariumController {
    private final AquariumService svc;
    public AquariumController(AquariumService svc) {
        this.svc = svc;
    }

    // 어항 등록
    @PostMapping("/register")
    public Aquarium register(@RequestBody Map<String, String> body) {
        return svc.register(body.get("name"), body.get("owner"));
    }

    // 어항 목록 조회
    @GetMapping("/list")
    public List<Aquarium> list() {
        return svc.listAll();
    }
}
