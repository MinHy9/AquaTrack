package com.example.aquarium.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquarium.web.entity.Aquarium;
import com.example.aquarium.web.entity.WaterQualityLog;
import com.example.aquarium.web.service.AquariumService;
import com.example.aquarium.web.service.WaterQualityLogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class WaterQualityController {
	
	private final AquariumService aquariumService;
	private final WaterQualityLogService waterQualityLogService;
	
	@PostMapping("{userid}/aquariums/waterqualitylogs")
	public ResponseEntity<List<WaterQualityLog>> recordWaterQualityLog(
	    @PathVariable("userid") Long userid, 
	    @RequestBody @Valid List<WaterQualityLog> requestLogs) {

	    // 유저의 모든 aquariums 가져오기
	    List<Aquarium> aquariums = aquariumService.getMyAquariums(userid);

	    // 각 로그에 대해 해당하는 aquariumId에 맞는 aquarium 객체 찾기
	    for (WaterQualityLog waterLog : requestLogs) {
	        for (Aquarium aquarium : aquariums) {
	            // Aquarium 객체의 ID를 가져와서 비교
	            if (aquarium.getAquariumId().equals(waterLog.getAquarium().getAquariumId())) {
	                waterLog.setAquarium(aquarium);  // 해당 수조에 연결
	                break;
	            }
	        }
	    }
	    // 서비스로 로그 저장 요청
	    return waterQualityLogService.createWaterQualityLogs(requestLogs);
	}
//	@DeleteMapping("/{userid}/aquariums/waterqualitylogs/{logid}")
//    public ResponseEntity<String> deleteLog(@PathVariable("userid") Long userid, 
//                                            @PathVariable("logid") Long logid) {
//        // 로그 삭제 처리
//        return waterQualityLogService.deleteWaterQualityLogById(logid);
//    }
		
}
