package com.example.aquarium.web.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.aquarium.web.entity.Alert;
import com.example.aquarium.web.entity.Aquarium;
import com.example.aquarium.web.entity.WaterQualityLog;
import com.example.aquarium.web.repository.WaterQualityLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaterQualityLogService {
	
	private final WaterQualityLogRepository waterQualityLogRepository;
	private final UserService userService;
	
	public List<WaterQualityLog> checkWaterQualityLogAndMakeAbnormalLogs(Long userId) {
	    List<Aquarium> aquariums = userService.findUserById(userId).getAquariums();
	    List<WaterQualityLog> abnormalLogs = new ArrayList<>();

	    for (Aquarium aquarium : aquariums) {
	        List<WaterQualityLog> recentLogs = findRecentLogsByAquarium(aquarium, 5); // 5분 내 로그

	        for (WaterQualityLog log : recentLogs) {
	            if (log.getTemperature() < 23.0 || log.getTemperature() > 28.0 ||
	                log.getTurbidity() > 300.0 ||
	                log.getPh() < 6.5 || log.getPh() > 8.0) {
	                abnormalLogs.add(log);
	            }
	        }
	    }
	    return abnormalLogs;
	}


//	public WaterQualityLog findLatestLog(Aquarium aquarium) {
//	    return waterQualityLogRepository.findTopByAquariumOrderByRecordedAtDesc(aquarium);
//	}
	public List<WaterQualityLog> findRecentLogsByAquarium(Aquarium aquarium, int minutes) {
	    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(minutes);
	    return waterQualityLogRepository.findByAquariumAndRecordedAtAfter(aquarium, cutoff);
	}
	public ResponseEntity<List<WaterQualityLog>> createWaterQualityLogs(List<WaterQualityLog> requestLogs) {
	    List<WaterQualityLog> savedLogs = waterQualityLogRepository.saveAll(requestLogs);
	    return ResponseEntity.ok(savedLogs);
	}
	public ResponseEntity<String> deleteWaterQualityLogById(Long userid){
	    if (!waterQualityLogRepository.existsById(userid)) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저가 없습니다.");
	    }
	    waterQualityLogRepository.deleteById(userid);
	    return ResponseEntity.ok("user 삭제 완료");
	}
	
	public String generateMessage(WaterQualityLog log) {
	    StringBuilder message = new StringBuilder();
	    Aquarium aquarium = log.getAquarium();
	    Long id = aquarium.getAquariumId();
	    message.append(id + "번 수조에서 경고 발생");
	    if (log.getTemperature() < 23.0 || log.getTemperature() > 28.0) {
	        message.append(String.format("현재 온도 %.1f°C\n", log.getTemperature()));
	    }
	    if (log.getTurbidity() > 300.0) {
	        message.append(String.format("현재 탁도 %.1f NTU\n", log.getTurbidity()));
	    }
	    if (log.getPh() < 6.5 || log.getPh() > 8.0) {
	        message.append(String.format("현재 ph %.1f\n", log.getPh()));
	    }

	    return message.toString();
	}
	
	public List<Alert.AlertType> generateAlertTypes(WaterQualityLog log) {
	    List<Alert.AlertType> types = new ArrayList<>();

	    if (log.getTemperature() < 23.0 || log.getTemperature() > 28.0)
	        types.add(Alert.AlertType.TEMPARATURE_WARNING);
	    if (log.getTurbidity() > 300.0)
	        types.add(Alert.AlertType.TURBIDITY_WARNING);
	    if (log.getPh() < 6.5 || log.getPh() > 8.0)
	        types.add(Alert.AlertType.PH_WARNING);

	    return types;
	}

	public List<WaterQualityLog> findByAquarium(Aquarium aquarium) {
		return waterQualityLogRepository.findByAquarium(aquarium);
	}
	

}
