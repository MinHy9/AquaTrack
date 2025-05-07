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
public class FeedingScheduleIntegrationTest {

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
        if (!userRepository.existsByEmail("feeder@example.com")) {
            User user = new User();
            user.setEmail("feeder@example.com");
            user.setPassword(passwordEncoder.encode("test1234"));
            user.setUsername("feeder");
            user.setPhone("01000000000");
            userRepository.save(user);
        }
    }

    @Test
    @WithMockUser(username = "feeder@example.com", roles = "USER")
    void testAquariumRegistration() throws Exception {
        AquariumRequest request = new AquariumRequest();
        request.setName("My First Aquarium");

        mockMvc.perform(post("/api/aquariums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}


