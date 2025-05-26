package com.aquatrack.aquarium.service;

import com.aquatrack.aquarium.dto.AquariumRequest;
import com.aquatrack.aquarium.dto.AquariumThresholdUpdateRequest;
import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.user.entity.User;
import com.aquatrack.aquarium.repository.AquariumRepository;
import com.aquatrack.user.repository.UserRepository;
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

        Float minTemp = 24.0f;
        Float maxTemp = 27.0f;
        Float minPH = 6.5f;
        Float maxPH = 8.0f;
        Float maxTurbidity = 300.0f;

        if (request.getFishName().equalsIgnoreCase("금붕어")) {
            minTemp = 18.0f;
            maxTemp = 24.0f;
            minPH = 7.0f;
            maxPH = 8.5f;
            maxTurbidity = 250.0f;
        }

        Aquarium aquarium = Aquarium.builder()
                .name(request.getName())
                .fishName(request.getFishName())
                .customMinTemperature(minTemp)
                .customMaxTemperature(maxTemp)
                .customMinPH(minPH)
                .customMaxPH(maxPH)
                .customMaxTurbidity(maxTurbidity)
                .user(user)
                .build();

        return aquariumRepository.save(aquarium);
    }

    //모든 어항 조회
    public List<Aquarium> getMyAquariums(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        return aquariumRepository.findByUser(user);
    }

    //id로 어항찾기
    public Long getAquariumIdByUser(Long userId) {
        return aquariumRepository.findByUser_UserId(userId)
                .map(aquarium -> aquarium.getAquariumId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 어항이 없습니다."));
    }

    //기준값 수정 API
    public void updateThresholds(Long aquariumId, AquariumThresholdUpdateRequest req) {
        Aquarium a = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다."));

        a.setCustomMinTemperature(req.getMinTemperature());
        a.setCustomMaxTemperature(req.getMaxTemperature());
        a.setCustomMinPH(req.getMinPH());
        a.setCustomMaxPH(req.getMaxPH());
        a.setCustomMaxTurbidity(req.getMaxTurbidity());

        aquariumRepository.save(a);
    }
}
