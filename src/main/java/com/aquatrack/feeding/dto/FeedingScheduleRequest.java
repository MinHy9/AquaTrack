package com.aquatrack.feeding.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedingScheduleRequest {
    private Long aquariumId;
    private List<String> timeList;
}
