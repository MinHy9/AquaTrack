package com.example.aquarium.web.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.aquarium.web.entity.User;
import com.example.aquarium.web.exception.UsernameNotFoundException;
import com.example.aquarium.web.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	
	public boolean isUserExists(Long userId) {
        return userRepository.existsById(userId);
    }
	
	public ResponseEntity<String> deleteUserById(Long userid){
	    if (!userRepository.existsById(userid)) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저가 없습니다.");
	    }
	    userRepository.deleteById(userid);
	    return ResponseEntity.ok("user 삭제 완료");
	}

	 public User createUser(User user) {
	        return userRepository.save(user);
	 }
	 
	 public User findUserById(Long userId) {
		    return userRepository.findById(userId)
		            .orElseThrow(() -> new UsernameNotFoundException("해당 ID의 유저를 찾을 수 없습니다: " + userId));
	}
	 
	 public List<User> findAllUser(){
		 return userRepository.findAll();
	 }
	 public List<User> findUsersHasUnresolvedAlerts(){
		 return userRepository.findDistinctByAlertsResolvedFalse();
	 }
	 public User findUserByUseranme(String username) {
		 return userRepository.findByUsername(username);
	 }
	 

}
