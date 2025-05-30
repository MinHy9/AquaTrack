package com.aquatrack.feeding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedingScheduleRequest {
    private Long aquariumId;
    private String time; //기존 방식

    // 새 방식 (맞춤 설정용)
    private List<String> feedingTimes;
}
