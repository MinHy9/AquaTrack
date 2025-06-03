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
        String timeStr = now.toString(); // 예: "12:30"
        List<FeedingSchedule> dueSchedules = scheduleRepository.findByTime(timeStr);

        for (FeedingSchedule schedule : dueSchedules) {
            String boardId = schedule.getAquarium().getBoardId();

            if (feedingStateService.isAutoFeedingEnabled(schedule.getAquarium().getAquariumId())) {
                mqttService.publishToDevice(boardId, "feeding", "feed_now");
                System.out.println("[자동급여] " + now + " → 보드 " + boardId + "에 feed_now 명령 전송");
            } else {
                System.out.println("[자동급여 차단] " + now + " → 보드 " + boardId + "은 자동급여 비활성화 상태");
            }
        }
    }
}
