
package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class HealthController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "pet-backend");
        
        try {
            redisTemplate.opsForValue().set("health_check", "ok");
            String value = redisTemplate.opsForValue().get("health_check");
            status.put("redis", value != null ? "UP" : "DOWN");
        } catch (Exception e) {
            status.put("redis", "DOWN");
        }
        
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseEntity.ok(ApiResponse.success("Hello from Spring Boot!"));
    }
}
