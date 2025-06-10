package com.aquatrack.dashboard.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertHistoryResponse {
    private String alertType; // 예: TEMPERATURE, PH, TURBIDITY
    private String message;
    private LocalDateTime createdAt;
}

