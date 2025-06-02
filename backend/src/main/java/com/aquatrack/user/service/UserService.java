package com.aquatrack.user.service;

import com.aquatrack.email.service.EmailService;
import com.aquatrack.user.dto.UserLoginRequest;
import com.aquatrack.user.dto.UserRegisterRequest;
import com.aquatrack.user.entity.User;
import com.aquatrack.common.security.JwtTokenProvider;
import com.aquatrack.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;


    //회원가입 기능
    public void register(UserRegisterRequest request) {
        // 이메일/아이디 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        /*if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }*/

        // 1. 비밀번호 형식 검사
        if (!isValidPasswordFormat(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 영문자, 숫자를 포함하여 8자 이상이어야 합니다.");
        }

        // 2. 같은 문자 4번 이상 반복 검사
        if (hasRepeatedCharacters(request.getPassword())) {
            throw new IllegalArgumentException("같은 문자를 4번 이상 연속해서 사용할 수 없습니다.");
        }


        // 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .phone(request.getPhone())
                .build();

        userRepository.save(newUser);
    }
    //이메일 중복검사 버튼
    public boolean isEmailDuplicated(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    //로그인 기능
    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 → JWT 발급
        return jwtTokenProvider.createToken(user.getEmail());
    }

    //비밀번호 변경
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        System.out.println("🔑 비밀번호 변경 요청 → userId: " + userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 사용자를 찾을 수 없습니다."));

        String oldPassword = user.getPassword();
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        System.out.println("✅ 변경 완료!");
        System.out.println("🔒 이전 암호화 비밀번호: " + oldPassword);
        System.out.println("🔐 새 암호화 비밀번호: " + encodedNewPassword);
    }

    //비밀번호 리셋
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        String tempPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, tempPassword);
    }
    //임시비밀번호생성
    private String generateRandomPassword() {
        int length = 10;
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return password.toString();
    }

    private boolean isValidPasswordFormat(String password) {
        return password != null && password.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
    }

    private boolean hasRepeatedCharacters(String password) {
        return password != null && password.matches(".*(.)\\1{3,}.*");
    }



}
