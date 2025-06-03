package com.aquatrack.dashboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AquariumThresholdResponse {
    private String fishName;
    private Float minTemperature;
    private Float maxTemperature;
    private Float minPH;
    private Float maxPH;
    private Float maxTurbidity;
}

