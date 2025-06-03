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
    public FeedingSchedule registerSchedule(FeedingScheduleRequest request, String email) {
        Aquarium aquarium = aquariumRepository.findById(request.getAquariumId())
                .filter(a -> a.getUser().getEmail().equals(email))
                .orElseThrow(() -> new RuntimeException("어항이 없습니다"));

        FeedingSchedule schedule = FeedingSchedule.builder()
                .aquarium(aquarium)
                .time(request.getTime())
                .enabled(true)
                .build();

        return scheduleRepository.save(schedule);
    }
    //사용자지정 급여시간 등록
    public void registerMultipleSchedules(List<FeedingScheduleRequest> requests, String email) {
        for (FeedingScheduleRequest req : requests) {
            Aquarium aquarium = aquariumRepository.findById(req.getAquariumId())
                    .filter(a -> a.getUser().getEmail().equals(email))
                    .orElseThrow(() -> new RuntimeException("어항 없음"));

            // 중복 체크는 필요시 추가 가능
            FeedingSchedule schedule = FeedingSchedule.builder()
                    .aquarium(aquarium)
                    .time(req.getTime())
                    .enabled(true)
                    .build();

            scheduleRepository.save(schedule);
        }
    }

    // 수동 급여 실행
    public boolean feedNow(String userId, Long aquariumId) {
        boolean canFeed = feedingStateService.isAutoFeedingEnabled(aquariumId);
        if (!canFeed) return false;

        String boardId = aquariumRepository.findById(aquariumId)
            .orElseThrow(() -> new RuntimeException("어항을 찾을 수 없습니다: " + aquariumId))
            .getBoardId();
        mqttService.publishToDevice(boardId, "feeding", "feed_now");
        return true;
    }

    public List<FeedingSchedule> getSchedules(Long aquariumId, String email) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .filter(a -> a.getUser().getEmail().equals(email))
                .orElseThrow(() -> new RuntimeException("해당 어항은 현재 사용자 소유가 아닙니다."));
        return scheduleRepository.findByAquariumAndEnabledTrue(aquarium);
    }
}
