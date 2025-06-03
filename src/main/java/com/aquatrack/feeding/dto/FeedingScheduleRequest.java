package com.aquatrack.feeding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedingScheduleRequest {
    private Long aquariumId;
    private String time;
}
