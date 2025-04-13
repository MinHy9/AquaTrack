package com.example.AquaTrack;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

public class TemperatureMonitor {

    private final TemperatureService temperatureService;

    public TemperatureMonitor(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @Scheduled(fixedRate = 5000) // 5초마다 체크
    public void simulateTemperatureReading() {
        double currentTemperature = Math.random() * 40; // 임시 값
        System.out.println("현재 온도: " + currentTemperature + "°C");
        temperatureService.checkTemperature(currentTemperature);
    }
}
