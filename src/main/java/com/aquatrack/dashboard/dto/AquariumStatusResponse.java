package com.aquatrack.dashboard.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AquariumStatusResponse {
    private String fishName;

    private Float temperature;
    private String temperatureStatus;

    private Float pH;
    private String pHStatus;

    private Float turbidity;
    private String turbidityStatus;

    private LocalDateTime recordedAt;
}

