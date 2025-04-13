package com.example.AquaTrack;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/temperature")
public class TemperatureController {

    private final TemperatureService temperatureService;

    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @PostMapping("/set")
    public String setTargetTemperature(@RequestParam double target) {
        temperatureService.setTargetTemperature(target);
        return "Target temperature set to " + target + "°C";
    }

    @GetMapping("/status")
    public String getFanStatus() {
        return "Fan is " + (temperatureService.isFanOn() ? "ON" : "OFF");
    }
}
