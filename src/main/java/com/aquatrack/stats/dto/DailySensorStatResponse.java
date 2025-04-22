package com.aquatrack.stats.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailySensorStatResponse {
    private Object dateLabel; // 일간: LocalDate, 주간/월간: String (DATE_FORMAT 결과)
    private Double temperatureAvg;
    private Double pHAvg;
    private Double turbidityAvg;
}
