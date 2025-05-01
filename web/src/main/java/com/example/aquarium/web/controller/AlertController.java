package com.example.aquarium.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquarium.web.service.AlertService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/alert")
@RequiredArgsConstructor
public class AlertController {
	private final AlertService alertService;
	
	@DeleteMapping("{alertid}")
	  public ResponseEntity<String> deleteAlert(@PathVariable("alertid") Long alertid) {
        return alertService.deleteAlertById(alertid);
    }
	
}
