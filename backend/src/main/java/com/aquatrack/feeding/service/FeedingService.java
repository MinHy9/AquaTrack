package com.aquatrack.feeding.service;

import com.aquatrack.common.security.CustomUserDetails;
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
    public FeedingSchedule registerSchedule(FeedingScheduleRequest request, CustomUserDetails userDetails) {
        Aquarium aquarium = aquariumRepository.findById(request.getAquariumId())
                .filter(a -> a.getUser().getUserId().equals(userDetails.getUser().getUserId()))
                .orElseThrow(() -> new RuntimeException("어항이 없습니다"));

        FeedingSchedule schedule = FeedingSchedule.builder()
                .aquarium(aquarium)
                .time(request.getTime())
                .enabled(true)
                .build();

        return scheduleRepository.save(schedule);
    }
    //사용자지정 급여시간 등록
    public void registerMultipleSchedules(Long aquariumId, List<String> times, CustomUserDetails userDetails) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .filter(a -> a.getUser().getUserId().equals(userDetails.getUser().getUserId()))
                .orElseThrow(() -> new RuntimeException("어항이 없습니다"));

        scheduleRepository.deleteByAquarium(aquarium);

        for (String time : times) {
            boolean exists = scheduleRepository.existsByAquariumAndTime(aquarium, time);
            if (exists) continue; // 이미 있으면 저장 안함

            FeedingSchedule schedule = FeedingSchedule.builder()
                    .aquarium(aquarium)
                    .time(time)
                    .enabled(true)
                    .build();
            scheduleRepository.save(schedule);
        }
    }

    // 수동 급여 실행
    public boolean feedNow(String userId, Long aquariumId) {
        boolean canFeed = feedingStateService.isAutoFeedingEnabled(aquariumId);
        if (!canFeed) return false;

        mqttService.publishToDevice(userId, aquariumId, "feeding", "feed_now");
        return true;
    }

    public List<FeedingSchedule> getSchedules(Long aquariumId, CustomUserDetails userDetails) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .filter(a -> a.getUser().getUserId().equals(userDetails.getUser().getUserId()))
                .orElseThrow(() -> new RuntimeException("해당 어항은 현재 사용자 소유가 아닙니다."));
        return scheduleRepository.findByAquariumAndEnabledTrue(aquarium);
    }
}
