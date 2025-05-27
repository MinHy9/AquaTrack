package com.aquatrack.user.service;

import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aquatrack.common.security.JwtTokenProvider;
import com.aquatrack.email.service.EmailService;
import com.aquatrack.user.dto.ChangeRandomPasswordRequest;
import com.aquatrack.user.dto.PasswordResetRequest;
import com.aquatrack.user.dto.UserLoginRequest;
import com.aquatrack.user.dto.UserRegisterRequest;
import com.aquatrack.user.entity.User;
import com.aquatrack.user.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    //회원가입 기능
    public void register(UserRegisterRequest request) {
        // 이메일/아이디 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
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

    //로그인 기능
    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 → JWT 발급
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
        return jwtTokenProvider.createToken(user.getEmail());
    }
    
    //임시 비빌번호 셍성
    public void generateAndSendTempPassword(PasswordResetRequest request) {
    	User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
    	 
    	String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    	StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for(int i= 0;i<10;i++) {
        	 sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        
        String encodedPassword = passwordEncoder.encode(sb.toString());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        emailService.sendTempPassword(request.getEmail(), sb.toString());
    }
    
    //임시 비밀번호 변경
    public void changeRandomPassword(String email,@Valid ChangeRandomPasswordRequest request) {
    	User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
    	String changedPassword = request.getPassword();
    	String encodedPassword = passwordEncoder.encode(changedPassword);
    	user.setPassword(encodedPassword);
    	userRepository.save(user);
    }

}
