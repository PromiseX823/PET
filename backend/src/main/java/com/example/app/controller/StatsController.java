package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final AdoptionRepository adoptionRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        long totalPets = petRepository.count();
        long pendingPets = petRepository.findByStatus("待领养").size();
        long totalUsers = userRepository.count();
        long totalPhotos = photoRepository.count();
        long totalAdoptions = adoptionRepository.count();
        
        Map<String, Object> stats = Map.of(
            "pets", Map.of("available", pendingPets, "total", totalPets),
            "adoptions", Map.of("total", totalAdoptions, "approved", 0, "completed", 0),
            "photos", Map.of("total", totalPhotos),
            "users", Map.of("total", totalUsers)
        );
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
