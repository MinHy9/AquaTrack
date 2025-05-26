package com.example.testaqua;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SensorController {
    private double tempMin = 18.0;
    private double tempMax = 30.0;
    private double phMin = 6.5;
    private double phMax = 8.0;
    private double turbMax = 5.0;

    private String evaluateStatus(double t, double p, double tu) {
        if (t < tempMin || t > tempMax || p < phMin || p > phMax || tu > turbMax) {
            return "danger";
        }
        return "normal";
    }

    @PostMapping("/api/config")
    public void setConfig(@RequestBody Map<String, Object> config) {
        tempMin = Double.parseDouble(config.get("tempMin").toString());
        tempMax = Double.parseDouble(config.get("tempMax").toString());
        phMin   = Double.parseDouble(config.get("phMin").toString());
        phMax   = Double.parseDouble(config.get("phMax").toString());
        turbMax = Double.parseDouble(config.get("turbMax").toString());
        System.out.println("새 기준값 설정됨: temp=" + tempMin + "~" + tempMax + ", pH=" + phMin + "~" + phMax + ", turbMax=" + turbMax);
    }


    @GetMapping("/api/data")
    public Map<String, Object> getSensorData() {
        double temp      = 24.5;  // 실제 센서 연동 시 교체
        double ph        = 7.2;
        double turbidity = 2.8;
        String status    = evaluateStatus(temp, ph, turbidity);

        Map<String, Object> data = new HashMap<>();
        data.put("temperature", temp);
        data.put("ph", ph);
        data.put("turbidity", turbidity);
        data.put("status", status);
        return data;
    }
}
