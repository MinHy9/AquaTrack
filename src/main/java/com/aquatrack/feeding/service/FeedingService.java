package com.aquatrack.feeding.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.aquarium.repository.AquariumRepository;
import com.aquatrack.common.mqtt.MqttService;
import com.aquatrack.feeding.dto.FeedingScheduleRequest;
import com.aquatrack.feeding.entity.FeedingSchedule;
import com.aquatrack.feeding.repository.FeedingScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedingService {
    private final FeedingScheduleRepository scheduleRepository;
    private final AquariumRepository aquariumRepository;
    private final FeedingStateService feedingStateService;
    private final MqttService mqttService; // IoT 통신용 서비스

    // 자동 급여 스케줄 등록, 프런트 쪽에서 시간대 리스트로 전달하게 됨애 따라 리스트로 수정, 시간대가 1~3개에서 다음과 같이 수정
    public List<FeedingSchedule> registerSchedule(FeedingScheduleRequest request) {
        Aquarium aquarium = aquariumRepository.findById(request.getAquariumId())
                .orElseThrow(() -> new RuntimeException("어항이 없습니다"));

        List<FeedingSchedule> result = new ArrayList<>();
        for (String time : request.getTimeList()) {
            FeedingSchedule schedule = FeedingSchedule.builder()
                    .aquarium(aquarium)
                    .time(time)
                    .enabled(true)
                    .build();
            result.add(scheduleRepository.save(schedule));
        }

        return result;
    }

    // 수동 급여 실행
    public boolean feedNow(Long aquariumId) {
        boolean canFeed = feedingStateService.isAutoFeedingEnabled(aquariumId);
        if (!canFeed) return false;

        mqttService.publish("aquatrack/" + aquariumId + "/feeding", "feed_now");
        return true;
    }
    
    //주기 마지막 시간과 다음 먹이 급여 시간 반환 하게 반환 이전 코드는 모든 급식 주기 다 가져옴
    public Map<String, LocalDateTime> getSchedules(Long aquariumId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new RuntimeException("어항이 없습니다"));
        List<FeedingSchedule> all = scheduleRepository.findByAquariumAndEnabledTrue(aquarium);
        LocalDateTime now = LocalDateTime.now();

        FeedingSchedule last = all.stream()
                .filter(s -> s.getCreatedAt().isBefore(now))
                .max(Comparator.comparing(FeedingSchedule::getCreatedAt))
                .orElse(null);

        FeedingSchedule next = all.stream()
                .filter(s -> s.getCreatedAt().isAfter(now))
                .min(Comparator.comparing(FeedingSchedule::getCreatedAt))
                .orElse(null);

        Map<String, LocalDateTime> result = new HashMap<>();
        result.put("last", last != null ? last.getCreatedAt() : null);
        result.put("next", next != null ? next.getCreatedAt() : null);
        return result;
    }
    
    
}
