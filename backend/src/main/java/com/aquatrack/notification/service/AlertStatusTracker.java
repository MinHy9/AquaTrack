package com.aquatrack.notification.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertStatusTracker {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class StatusInfo {
        private LocalDateTime abnormalStartTime;
        private LocalDateTime lastAlertSentTime;
    }

    private final Map<Long, StatusInfo> aquariumStatusMap = new ConcurrentHashMap<>();

    // 최초 비정상 상태 감지 시
    public void updateAbnormalState(Long aquariumId, LocalDateTime detectedTime) {
        aquariumStatusMap.computeIfAbsent(aquariumId, id -> new StatusInfo(detectedTime, null));
    }


    // 알림 전송 시간 갱신
    public void updateAlertSentTime(Long aquariumId, LocalDateTime alertTime) {
        if (aquariumStatusMap.containsKey(aquariumId)) {
            aquariumStatusMap.get(aquariumId).setLastAlertSentTime(alertTime);
        }
    }

    // 상태 정보 조회
    public StatusInfo getStatusInfo(Long aquariumId) {
        return aquariumStatusMap.get(aquariumId);
    }

    // 정상 상태로 돌아오면 추적 제거
    public void clearNormalState(Long aquariumId) {
        aquariumStatusMap.remove(aquariumId);
    }

    // 비정상 상태인지 여부
    public boolean isAbnormal(Long aquariumId) {
        return aquariumStatusMap.containsKey(aquariumId);
    }

    // 전체 상태 확인 (디버그용)
    public Map<Long, StatusInfo> getAllTrackedAquariums() {
        return aquariumStatusMap;
    }
}
