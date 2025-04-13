package com.example.AquaTrack;

import org.springframework.stereotype.Service;

@Service
public class TemperatureService {
    private double targetTemperature = 25.0; // 기본값
    private boolean fanOn = false;

    public void setTargetTemperature(double target) {
        this.targetTemperature = target;
    }

    public boolean isFanOn() {
        return fanOn;
    }

    // 이 메서드는 주기적으로 호출된다고 가정
    public void checkTemperature(double currentTemperature) {
        if (currentTemperature > targetTemperature && !fanOn) {
            fanOn = true;
            System.out.println("냉각팬을 작동시킵니다.");
        } else if (currentTemperature <= targetTemperature && fanOn) {
            fanOn = false;
            System.out.println("냉각팬을 끕니다.");
        }
    }
}
