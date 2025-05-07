package com.aquatrack.sensor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaterQualityLogRequest {
    private Long aquariumId;
    private Float temperature;
    private Float pH;
    private Float turbidity;
}
