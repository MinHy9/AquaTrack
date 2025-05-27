package com.aquatrack.aquarium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AquariumThresholdUpdateRequest {
    private Float minTemperature;
    private Float maxTemperature;
    private Float minPH;
    private Float maxPH;
    private Float maxTurbidity;
}
