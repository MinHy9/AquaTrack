package com.aquatrack.integration;

import com.aquatrack.AquatrackBackendApplication;
import com.aquatrack.user.dto.UserLoginRequest;
import com.aquatrack.user.entity.User;
import com.aquatrack.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AquatrackBackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        if (!userRepository.existsByEmail("loginuser@example.com")) {
            User user = new User();
            user.setEmail("loginuser@example.com");
            user.setPassword(passwordEncoder.encode("password123")); // 반드시 인코딩!
            user.setUsername("loginuser");
            user.setPhone("01000000000");
            userRepository.save(user);
        }
    }

    @Test
    public void testLoginWithValidCredentials() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("loginuser@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(result -> {
                    System.out.println("응답 상태: " + result.getResponse().getStatus());
                    System.out.println("응답 본문: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("loginuser@example.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

