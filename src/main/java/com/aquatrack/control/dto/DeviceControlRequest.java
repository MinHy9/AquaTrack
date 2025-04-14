package com.aquatrack.control.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceControlRequest {
    private Long aquariumId;
    private boolean activate; // true = 켜기, false = 끄기
}
