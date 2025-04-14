package com.aquatrack.stats.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySensorStatResponse {
    private String periodLabel; // "2025-04-07", "2025-W15", "2025-04" 모두 가능
    private Double avgTemperature;
    private Double avgPH;
    private Double avgTurbidity;
}
