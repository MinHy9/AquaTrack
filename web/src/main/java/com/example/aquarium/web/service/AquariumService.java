package com.example.aquarium.web.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.aquarium.web.entity.Aquarium;
import com.example.aquarium.web.entity.User;
import com.example.aquarium.web.exception.AquariumNotFoundException;
import com.example.aquarium.web.exception.UsernameNotFoundException;
import com.example.aquarium.web.repository.AquariumRepository;
import com.example.aquarium.web.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AquariumService {
	
	private final UserRepository userRepository;
	private final AquariumRepository aquariumRepository;
	
	public Aquarium registerAquarium(Long userId, Aquarium aquariumRequest) {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
	    Aquarium aquarium = Aquarium.builder()
	            .user(user)
	            .registeredDate(aquariumRequest.getRegisteredDate())
	            .build();
	    return aquariumRepository.save(aquarium);
	}

	public List<Aquarium> getMyAquariums(Long id) {
		User user = userRepository.findById(id)
                	.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
	
		if (aquariumRepository.findByUser(user).isEmpty()) {
			throw new AquariumNotFoundException("해당 사용자의 수조가 존재하지 않습니다.");
		}
        return aquariumRepository.findByUser(user);
    }
}
