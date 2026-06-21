
package com.example.app.controller;

import com.example.app.dto.response.AdoptionResponse;
import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.response.CommentResponse;
import com.example.app.dto.response.PetResponse;
import com.example.app.dto.response.PhotoResponse;
import com.example.app.dto.response.UserResponse;
import com.example.app.entity.User;
import com.example.app.exception.BusinessException;
import com.example.app.repository.UserRepository;
import com.example.app.service.AdoptionService;
import com.example.app.service.CommentService;
import com.example.app.service.FollowService;
import com.example.app.service.PetService;
import com.example.app.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PetService petService;
    private final PhotoService photoService;
    private final AdoptionService adoptionService;
    private final CommentService commentService;
    private final FollowService followService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable("user_id") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return ResponseEntity.ok(ApiResponse.success(toUserResponse(user)));
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable("user_id") Long userId,
            @RequestBody Map<String, Object> request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (request.containsKey("username")) {
            String username = (String) request.get("username");
            if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
                throw new BusinessException("用户名已存在");
            }
            user.setUsername(username);
        }

        if (request.containsKey("email")) {
            String email = (String) request.get("email");
            if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
                throw new BusinessException("邮箱已存在");
            }
            user.setEmail(email);
        }

        if (request.containsKey("phone")) {
            user.setPhone((String) request.get("phone"));
        }

        if (request.containsKey("address")) {
            user.setAddress((String) request.get("address"));
        }

        if (request.containsKey("bio")) {
            user.setBio((String) request.get("bio"));
        }

        if (request.containsKey("avatar_url")) {
            user.setAvatarUrl((String) request.get("avatar_url"));
        }

        user = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("用户信息更新成功", toUserResponse(user)));
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("user_id") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        userRepository.delete(user);
        return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
    }

    @PutMapping("/{user_id}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleUserStatus(@PathVariable("user_id") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        String newStatus = "enabled".equals(user.getStatus()) ? "disabled" : "enabled";
        user.setStatus(newStatus);
        user = userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("status", newStatus);

        return ResponseEntity.ok(ApiResponse.success(
                "disabled".equals(newStatus) ? "用户已禁用" : "用户已启用",
                result
        ));
    }

    @GetMapping("/{user_id}/pets")
    public ResponseEntity<ApiResponse<List<PetResponse>>> getUserPets(@PathVariable("user_id") Long userId) {
        // 验证用户存在
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("用户不存在");
        }
        List<PetResponse> pets = petService.getPetsByOwner(userId);
        return ResponseEntity.ok(ApiResponse.success(pets));
    }

    @GetMapping("/{user_id}/liked-photos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserLikedPhotos(@PathVariable("user_id") Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("用户不存在");
        }
        List<PhotoResponse> photos = photoService.getLikedPhotosByUser(userId);
        List<Long> likedPhotoIds = photos.stream().map(PhotoResponse::getId).toList();
        
        Map<String, Object> result = new HashMap<>();
        result.put("liked_photo_ids", likedPhotoIds);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{user_id}/collected-photos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserCollectedPhotos(@PathVariable("user_id") Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("用户不存在");
        }
        List<PhotoResponse> photos = photoService.getCollectedPhotosByUser(userId);
        List<Long> collectedPhotoIds = photos.stream().map(PhotoResponse::getId).toList();
        
        Map<String, Object> result = new HashMap<>();
        result.put("collected_photo_ids", collectedPhotoIds);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    private UserResponse toUserResponse(User user) {
        Long userId = user.getId();
        return UserResponse.builder()
                .id(userId)
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatarUrl())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .followingCount(followService.getFollowingCount(userId))
                .followerCount(followService.getFollowerCount(userId))
                .petCount(petService.getPetCountByOwner(userId))
                .photoCount(photoService.getPhotoCountByUser(userId))
                .description(user.getBio())
                .area(user.getAddress())
                .pets(petService.getPetsByOwner(userId))
                .photos(photoService.getPhotosByUser(userId))
                .applications(getUserAdoptions(userId))
                .comments(getUserComments(userId))
                .build();
    }
    
    private List<AdoptionResponse> getUserAdoptions(Long userId) {
        try {
            return adoptionService.getUserAdoptions(userId);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
    
    private List<CommentResponse> getUserComments(Long userId) {
        try {
            return commentService.getUserComments(userId);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}
