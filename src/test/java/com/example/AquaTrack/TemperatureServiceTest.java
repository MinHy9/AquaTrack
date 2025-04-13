package com.example.AquaTrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TemperatureServiceTest {

    private TemperatureService temperatureService;

    @BeforeEach
    void setUp() {
        temperatureService = new TemperatureService();
    }

    @Test
    void fanShouldTurnOnWhenTemperatureExceedsTarget() {
        temperatureService.setTargetTemperature(25.0);
        temperatureService.checkTemperature(30.0); // 현재 온도 > 목표 온도
        assertTrue(temperatureService.isFanOn(), "팬이 켜져야 합니다.");
    }

    @Test
    void fanShouldTurnOffWhenTemperatureDropsBelowTarget() {
        temperatureService.setTargetTemperature(25.0);
        temperatureService.checkTemperature(30.0); // 먼저 켠다
        temperatureService.checkTemperature(24.0); // 그다음 끈다
        assertFalse(temperatureService.isFanOn(), "팬이 꺼져야 합니다.");
    }

    @Test
    void targetTemperatureShouldBeSetProperly() {
        temperatureService.setTargetTemperature(22.5);
        temperatureService.checkTemperature(25.0);
        assertTrue(temperatureService.isFanOn(), "목표 온도 설정 후 팬 작동 여부 확인");
    }
}
