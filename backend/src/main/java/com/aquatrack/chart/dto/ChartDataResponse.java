package com.aquatrack.chart.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartDataResponse {
    private List<String> categories;
    private List<Double> temperature;
    private List<Double> ph;
    private List<Double> turbidity;
}

