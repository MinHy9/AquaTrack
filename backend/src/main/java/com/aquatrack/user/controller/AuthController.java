package com.aquatrack.user.controller;

import com.aquatrack.common.security.CustomUserDetails;
import com.aquatrack.user.dto.ChangeRandomPasswordRequest;
import com.aquatrack.user.dto.PasswordResetRequest;
import com.aquatrack.user.dto.UserLoginRequest;
import com.aquatrack.user.dto.UserRegisterRequest;
import com.aquatrack.user.entity.User;
import com.aquatrack.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailDuplicated(email);
        return ResponseEntity.ok(exists);
    }

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
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangeRandomPasswordRequest request
    ) {
        System.out.println("✅ [newPassword] 컨트롤러 진입");

        if (userDetails == null) {
            System.out.println("❌ 인증 정보 없음 (userDetails == null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        CustomUserDetails custom = (CustomUserDetails) userDetails;
        Long userId = custom.getUser().getUserId();
        System.out.println("🔑 인증된 사용자 ID: " + userId);

        try {
            userService.changePassword(userId, request.getNewPassword());
            return ResponseEntity.ok("비밀번호 변경 완료");
        } catch (Exception e) {
            System.out.println("❌ 예외 발생: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"서버 오류가 발생했습니다.\"}");
        }
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        userService.resetPassword(request.getEmail());
        return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
    }
}
