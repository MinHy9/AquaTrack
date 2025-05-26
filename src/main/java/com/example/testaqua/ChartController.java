package com.example.testaqua;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

@RestController
public class ChartController {

    @GetMapping("/api/chart/{period}")
    public Map<String, Object> getChartData(@PathVariable("period") String period) {
        Map<String, Object> response = new HashMap<>();

        if ("daily".equalsIgnoreCase(period)) {
            response.put("categories", Arrays.asList("00:00","04:00","08:00","12:00","16:00","20:00"));
            response.put("temperature", Arrays.asList(20.0, 24.2, 24.5, 24.3, 24.1, 24.0));
            response.put("ph", Arrays.asList(7.1, 7.2, 7.2, 7.3, 7.2, 7.2));
            response.put("turbidity", Arrays.asList(2.5, 2.6, 2.8, 2.7, 2.6, 2.8));
        } else if ("weekly".equalsIgnoreCase(period)) {
            response.put("categories", Arrays.asList("월", "화", "수", "목", "금", "토", "일"));
            response.put("temperature", Arrays.asList(24.2, 24.3, 24.5, 24.4, 24.2, 24.1, 24.3));
            response.put("ph", Arrays.asList(7.2, 7.1, 7.2, 7.3, 7.2, 7.2, 7.1));
            response.put("turbidity", Arrays.asList(2.6, 2.7, 2.8, 2.7, 2.6, 2.8, 2.7));
        } else if ("monthly".equalsIgnoreCase(period)) {
            response.put("categories", Arrays.asList("1주", "2주", "3주", "4주"));
            response.put("temperature", Arrays.asList(24.3, 24.4, 24.2, 24.5));
            response.put("ph", Arrays.asList(7.2, 7.1, 7.3, 7.2));
            response.put("turbidity", Arrays.asList(2.7, 2.6, 2.8, 2.7));
        } else {
            // 기본 daily 데이터를 반환
            response.put("categories", Arrays.asList("00:00","04:00","08:00","12:00","16:00","20:00"));
            response.put("temperature", Arrays.asList(24.0, 24.2, 24.5, 24.3, 24.1, 24.0));
            response.put("ph", Arrays.asList(7.1, 7.2, 7.2, 7.3, 7.2, 7.2));
            response.put("turbidity", Arrays.asList(2.5, 2.6, 2.8, 2.7, 2.6, 2.8));
        }
        return response;
    }
}
