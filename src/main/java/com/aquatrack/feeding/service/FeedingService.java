package com.aquatrack.feeding.service;

import com.aquatrack.feeding.dto.FeedingScheduleRequest;
import com.aquatrack.aquarium.entity.Aquarium;
import com.aquatrack.feeding.entity.FeedingSchedule;
import com.aquatrack.aquarium.repository.AquariumRepository;
import com.aquatrack.feeding.repository.FeedingScheduleRepository;
import com.aquatrack.common.mqtt.MqttService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedingService {
    private final FeedingScheduleRepository scheduleRepository;
    private final AquariumRepository aquariumRepository;
    private final FeedingStateService feedingStateService;
    private final MqttService mqttService; // IoT 통신용 서비스

    // 자동 급여 스케줄 등록
    public FeedingSchedule registerSchedule(FeedingScheduleRequest request) {
        Aquarium aquarium = aquariumRepository.findById(request.getAquariumId())
                .orElseThrow(() -> new RuntimeException("어항이 없습니다"));

        FeedingSchedule schedule = FeedingSchedule.builder()
                .aquarium(aquarium)
                .time(request.getTime())
                .enabled(true)
                .build();

        return scheduleRepository.save(schedule);
    }

    // 수동 급여 실행
    public boolean feedNow(Long aquariumId) {
        boolean canFeed = feedingStateService.isAutoFeedingEnabled(aquariumId);
        if (!canFeed) return false;

        mqttService.publish("aquatrack/" + aquariumId + "/feeding", "feed_now");
        return true;
    }

    public List<FeedingSchedule> getSchedules(Long aquariumId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new RuntimeException("어항이 없습니다"));
        return scheduleRepository.findByAquariumAndEnabledTrue(aquarium);
    }
}
