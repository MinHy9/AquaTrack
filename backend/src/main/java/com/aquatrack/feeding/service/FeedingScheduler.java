package com.aquatrack.feeding.service;

import com.aquatrack.feeding.entity.FeedingSchedule;
import com.aquatrack.feeding.repository.FeedingScheduleRepository;
import com.aquatrack.feeding.service.FeedingStateService;
import com.aquatrack.common.mqtt.MqttService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedingScheduler {

    private final FeedingScheduleRepository scheduleRepository;
    private final FeedingStateService feedingStateService;
    private final MqttService mqttService;

    @Scheduled(fixedRate = 60000) // 매 1분마다 실행
    public void executeFeedingSchedules() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<FeedingSchedule> dueSchedules = scheduleRepository.findByTime(now);

        for (FeedingSchedule schedule : dueSchedules) {
            Long aquariumId = schedule.getAquarium().getAquariumId();

            if (feedingStateService.isAutoFeedingEnabled(aquariumId)) {
                mqttService.publish("aquatrack/" + aquariumId + "/feeding", "feed_now");
                System.out.println("[자동급여] " + now + " → 어항 " + aquariumId + "에 feed_now 명령 전송");
            } else {
                System.out.println("[자동급여 차단] " + now + " → 어항 " + aquariumId + "은 자동급여 비활성화 상태");
            }
        }
    }
}
