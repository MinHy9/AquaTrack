package com.aquatrack.dashboard.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LatestSensorDataResponse {
    private Float temperature;
    private Float pH;
    private Float turbidity;
    private LocalDateTime recordedAt;
}

