package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.response.FollowResponse;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import com.example.app.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserRepository userRepository;

    /**
     * 关注用户
     */
    @PostMapping("/{following_id}")
    public ResponseEntity<ApiResponse<FollowResponse>> followUser(@PathVariable("following_id") Long followingId) {
        Long followerId = getCurrentUserId();
        FollowResponse response = followService.followUser(followerId, followingId);
        return ResponseEntity.ok(ApiResponse.success("关注成功", response));
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{following_id}")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(@PathVariable("following_id") Long followingId) {
        Long followerId = getCurrentUserId();
        followService.unfollowUser(followerId, followingId);
        return ResponseEntity.ok(ApiResponse.success("取消关注成功", null));
    }

    /**
     * 切换关注状态
     */
    @PostMapping("/toggle/{following_id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleFollow(@PathVariable("following_id") Long followingId) {
        Long followerId = getCurrentUserId();
        Map<String, Object> result = followService.toggleFollow(followerId, followingId);
        return ResponseEntity.ok(ApiResponse.success("操作成功", result));
    }

    /**
     * 获取某人的关注列表
     */
    @GetMapping("/following/{user_id}")
    public ResponseEntity<ApiResponse<List<FollowResponse>>> getFollowingList(@PathVariable("user_id") Long userId) {
        List<FollowResponse> list = followService.getFollowingList(userId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 获取某人的粉丝列表
     */
    @GetMapping("/followers/{user_id}")
    public ResponseEntity<ApiResponse<List<FollowResponse>>> getFollowerList(@PathVariable("user_id") Long userId) {
        List<FollowResponse> list = followService.getFollowerList(userId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 获取关注统计
     */
    @GetMapping("/stats/{user_id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFollowStats(@PathVariable("user_id") Long userId) {
        Long currentUserId = getCurrentUserIdOrNull();
        Map<String, Object> stats = followService.getFollowStats(userId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * 检查是否已关注
     */
    @GetMapping("/check/{following_id}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkFollowing(@PathVariable("following_id") Long followingId) {
        Long followerId = getCurrentUserId();
        boolean isFollowing = followService.isFollowing(followerId, followingId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("following", isFollowing)));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new com.example.app.exception.BusinessException("未登录");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new com.example.app.exception.BusinessException("用户不存在"));  
    }

    private Long getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
