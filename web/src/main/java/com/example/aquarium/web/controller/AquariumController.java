package com.example.aquarium.web.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquarium.web.entity.Aquarium;
import com.example.aquarium.web.service.AquariumService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/{userid}")
@RequiredArgsConstructor
public class AquariumController {
	
	private final AquariumService aquariumService;
	@PostMapping("/aquarium")
	public ResponseEntity<Aquarium> createAquarium(@PathVariable("userid") Long userid, @RequestBody Aquarium aquariumRequest) {
	    Aquarium aquarium = aquariumService.registerAquarium(userid, aquariumRequest);
	    return ResponseEntity.ok(aquarium); 
	}

	@GetMapping("/aquariums")
	public List<Aquarium> findMyAquarium(@PathVariable("userid") Long userid) {
	    return aquariumService.getMyAquariums(userid);
	}	
}
