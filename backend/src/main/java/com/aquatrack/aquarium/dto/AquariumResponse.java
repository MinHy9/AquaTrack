package com.aquatrack.aquarium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AquariumResponse {
    private Long aquariumId;
    private String name;
    private String fishName;
    private String boardId;
}
