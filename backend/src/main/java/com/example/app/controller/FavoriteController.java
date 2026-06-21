
package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.entity.Pet;
import com.example.app.entity.User;
import com.example.app.repository.PetRepository;
import com.example.app.repository.UserRepository;
import com.example.app.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    @PostMapping("/pets/{pet_id}")
    public ResponseEntity<ApiResponse<Void>> favoritePet(@PathVariable("pet_id") Long petId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        favoriteService.favoritePet(petId, user.getId());
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

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Pet>>> getMyFavorites() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        List<Pet> favoritePets = favoriteService.getUserFavorites(user.getId());
        return ResponseEntity.ok(ApiResponse.success(favoritePets));
    }
}
