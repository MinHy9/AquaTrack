package com.example.aquarium.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquarium.web.entity.User;
import com.example.aquarium.web.repository.UserRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user){
		userRepository.save(user);
		return ResponseEntity.ok(user);
	}
	@DeleteMapping("/{userid}")
	public ResponseEntity<String> deleteUserById(@PathVariable("userid") int userid){
		if (!userRepository.existsById(userid)) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저가 없습니다.");
	    }
	    userRepository.deleteById(userid);
	    return ResponseEntity.ok("user 삭제 완료");
	}
}
