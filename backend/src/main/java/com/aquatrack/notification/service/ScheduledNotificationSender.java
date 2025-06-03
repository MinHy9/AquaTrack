package com.aquatrack.notification.service;

import com.aquatrack.common.mqtt.MqttService;
import com.aquatrack.sensor.entity.WaterQualityLog;
import com.aquatrack.sensor.repository.WaterQualityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledNotificationSender {

    private final WaterQualityLogRepository waterQualityLogRepository;
    private final SmsService smsService;
    private final AlertStatusTracker alertStatusTracker;

    @Scheduled(fixedRate = 60000) // 1분마다 검사
    public void checkPersistentAbnormalStates() {
        System.out.println("🔁 [Scheduler] Checking for persistent abnormal states...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesAgo = now.minusMinutes(10);
        List<WaterQualityLog> logs = waterQualityLogRepository.findByRecordedAtAfter(tenMinutesAgo.atZone(ZoneId.systemDefault()).toInstant());

        for (WaterQualityLog log : logs) {
            if (log.getAquarium() == null || log.getAquarium().getUser() == null) continue;

            Long aquariumId = log.getAquarium().getAquariumId();
            double temperature = log.getTemperature();
            double pH = log.getPH();
            double turbidity = log.getTurbidity();

            double tempMin = log.getAquarium().getCustomMinTemperature();
            double tempMax = log.getAquarium().getCustomMaxTemperature();
            double phMin = log.getAquarium().getCustomMinPH();
            double phMax = log.getAquarium().getCustomMaxPH();
            double turbidityMax = log.getAquarium().getCustomMaxTurbidity();

            boolean isAbnormal = temperature < tempMin || temperature > tempMax ||
                    pH < phMin || pH > phMax ||
                    turbidity > turbidityMax;

            if (isAbnormal) {
                AlertStatusTracker.StatusInfo status = alertStatusTracker.getStatusInfo(aquariumId);

                if (status == null) {
                    alertStatusTracker.updateAbnormalState(aquariumId, log.getRecordedAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    continue;
                }

                boolean overTenMin = status.getAbnormalStartTime().isBefore(tenMinutesAgo);
                boolean needsResend = status.getLastAlertSentTime() == null ||
                        status.getLastAlertSentTime().isBefore(tenMinutesAgo);

                if (overTenMin && needsResend) {
                    String phone = log.getAquarium().getUser().getPhone();
                    if (temperature < tempMin || temperature > tempMax) {
                        String msg = String.format("[재경고] 현재 수온은 %.1f°C로 정상 범위를 벗어났습니다. 빨리 조치를 취해주세요.", temperature);
                        smsService.sendSms(phone, msg);
                    }

                    if (pH < phMin || pH > phMax) {
                        String msg = String.format("[재경고] 현재 pH는 %.1f로 정상 범위를 벗어났습니다. 빨리 조치를 취해주세요.", pH);
                        smsService.sendSms(phone, msg);
                    }

                    if (turbidity > turbidityMax) {
                        String msg = String.format("[재경고] 현재 탁도는 %.1f NTU로 정상 범위를 벗어났습니다. 빨리 조치를 취해주세요.", turbidity);
                        smsService.sendSms(phone, msg);
                    }

                    alertStatusTracker.updateAlertSentTime(aquariumId, now);
                    System.out.println("📨 재경고 전송됨: " + phone);
                }

            } else {
                alertStatusTracker.clearNormalState(aquariumId); // 정상 상태 회복 시 초기화
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void sendScheduledControlSignals() {
        // mqttService.sendControlMessage(); (필요 시 사용)
    }
}
