package com.aquatrack.aquarium.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AquariumThresholdResponse {
    private Float minTemperature;
    private Float maxTemperature;
    private Float minPh;
    private Float maxPh;
    private Float minTurbidity;
    private Float maxTurbidity;
}
