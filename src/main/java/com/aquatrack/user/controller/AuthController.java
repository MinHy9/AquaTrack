package com.aquatrack.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aquatrack.user.dto.ChangeRandomPasswordRequest;
import com.aquatrack.user.dto.PasswordResetRequest;
import com.aquatrack.user.dto.UserLoginRequest;
import com.aquatrack.user.dto.UserRegisterRequest;
import com.aquatrack.user.entity.User;
import com.aquatrack.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegisterRequest request){
        userService.register(request);
        return ResponseEntity.ok("회원가입 완료!!");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid UserLoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }
    
    @PutMapping("/newPassword")
    public ResponseEntity<String> makeNewPassword(@AuthenticationPrincipal User user,
            @RequestBody @Valid ChangeRandomPasswordRequest request) {
    	userService.changeRandomPassword(user.getEmail(), request);
    	return ResponseEntity.ok("임시비밀번호 -> 새 비밀번호 변경완료!!");
    }
    
    @PutMapping("/randomPassword")
    public ResponseEntity<String> makeRandomPassword(@RequestBody @Valid PasswordResetRequest request){
    	userService.generateAndSendTempPassword(request);
    	return ResponseEntity.ok("임시비밀번호 생성 완료 메일 확인 하세요");
    }
    
    
}
