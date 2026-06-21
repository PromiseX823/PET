package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
        long adoptedPets = petRepository.findByStatus("已领养").size();
        long totalUsers = userRepository.count();
        long totalPhotos = photoRepository.count();
        long totalAdoptions = adoptionRepository.count();
        long approvedAdoptions = adoptionRepository.findByStatus("approved").size();
        long pendingAdoptions = adoptionRepository.findByStatus("pending").size();
        long rejectedAdoptions = adoptionRepository.findByStatus("rejected").size();
        long completedAdoptions = adoptionRepository.findByStatus("completed").size();

        Map<String, Object> petsStats = new HashMap<>();
        petsStats.put("available", pendingPets);
        petsStats.put("total", totalPets);
        petsStats.put("adopted", adoptedPets);

        Map<String, Object> adoptionsStats = new HashMap<>();
        adoptionsStats.put("total", totalAdoptions);
        adoptionsStats.put("approved", approvedAdoptions);
        adoptionsStats.put("pending", pendingAdoptions);
        adoptionsStats.put("rejected", rejectedAdoptions);
        adoptionsStats.put("completed", completedAdoptions);

        Map<String, Object> photosStats = new HashMap<>();
        photosStats.put("total", totalPhotos);

        Map<String, Object> usersStats = new HashMap<>();
        usersStats.put("total", totalUsers);
        usersStats.put("today", 0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("pets", petsStats);
        stats.put("adoptions", adoptionsStats);
        stats.put("photos", photosStats);
        stats.put("users", usersStats);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
