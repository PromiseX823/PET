
package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/pets/{pet_id}")
    public ResponseEntity<ApiResponse<Void>> favoritePet(
            @PathVariable("pet_id") Long petId,
            @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("user_id")).longValue();
        favoriteService.favoritePet(petId, userId);
        return ResponseEntity.ok(ApiResponse.success("收藏成功", null));
    }

    @DeleteMapping("/pets/{pet_id}")
    public ResponseEntity<ApiResponse<Void>> unfavoritePet(
            @PathVariable("pet_id") Long petId,
            @RequestParam Long user_id) {
        favoriteService.unfavoritePet(petId, user_id);
        return ResponseEntity.ok(ApiResponse.success("取消收藏成功", null));
    }

    @GetMapping("/pets/{pet_id}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFavoriteStatus(
            @PathVariable("pet_id") Long petId,
            @RequestParam Long user_id) {
        boolean isFavorited = favoriteService.isPetFavorited(petId, user_id);
        return ResponseEntity.ok(ApiResponse.success(Map.of("is_favorited", isFavorited)));
    }
}
