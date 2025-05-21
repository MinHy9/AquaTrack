package com.aquatrack.notification.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertStatusTracker {

    public static class StatusInfo {
        private LocalDateTime abnormalStartTime;
        private LocalDateTime lastAlertSentTime;

        public StatusInfo(LocalDateTime abnormalStartTime) {
            this.abnormalStartTime = abnormalStartTime;
        }

        public LocalDateTime getAbnormalStartTime() {
            return abnormalStartTime;
        }

        public LocalDateTime getLastAlertSentTime() {
            return lastAlertSentTime;
        }

        public void setLastAlertSentTime(LocalDateTime lastAlertSentTime) {
            this.lastAlertSentTime = lastAlertSentTime;
        }
    }

    private final Map<Long, StatusInfo> aquariumStatusMap = new ConcurrentHashMap<>();

    public void updateAbnormalState(Long aquariumId, LocalDateTime detectedTime) {
        aquariumStatusMap.putIfAbsent(aquariumId, new StatusInfo(detectedTime));
    }

    public void updateAlertSentTime(Long aquariumId, LocalDateTime alertTime) {
        if (aquariumStatusMap.containsKey(aquariumId)) {
            aquariumStatusMap.get(aquariumId).setLastAlertSentTime(alertTime);
        }
    }

    public StatusInfo getStatusInfo(Long aquariumId) {
        return aquariumStatusMap.get(aquariumId);
    }

    public void clearNormalState(Long aquariumId) {
        aquariumStatusMap.remove(aquariumId);
    }

    public boolean isAbnormal(Long aquariumId) {
        return aquariumStatusMap.containsKey(aquariumId);
    }
}
