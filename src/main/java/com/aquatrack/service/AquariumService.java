package com.aquatrack.service;

import com.aquatrack.dto.AquariumRequest;
import com.aquatrack.entity.Aquarium;
import com.aquatrack.entity.User;
import com.aquatrack.repository.AquariumRepository;
import com.aquatrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AquariumService {
    private final AquariumRepository aquariumRepository;
    private final UserRepository userRepository;

    public Aquarium register(String email, AquariumRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        Aquarium aquarium = Aquarium.builder()
                .name(request.getName())
                .user(user)
                .build();

        return aquariumRepository.save(aquarium);
    }

    public List<Aquarium> getMyAquariums(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        return aquariumRepository.findByUser(user);
    }
}
