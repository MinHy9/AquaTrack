package com.aquatrack.integration;

import com.aquatrack.AquatrackBackendApplication;
import com.aquatrack.aquarium.dto.AquariumRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AquatrackBackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AquariumRegistrationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        // 수조 등록 전에 반드시 유저가 있어야 한다면 이 부분 유지
        if (!userRepository.existsByEmail("testuser@example.com")) {
            User user = new User();
            user.setUsername("testuser");
            user.setEmail("testuser@example.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setPhone("01012345678");
            userRepository.save(user);
        }
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    public void testAquariumRegistration() throws Exception {
        AquariumRequest request = new AquariumRequest();
        request.setName("My First Tank");

        mockMvc.perform(post("/api/aquariums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

