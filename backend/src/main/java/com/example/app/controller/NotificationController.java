
package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.response.NotificationResponse;
import com.example.app.entity.Notification;
import com.example.app.entity.User;
import com.example.app.exception.BusinessException;
import com.example.app.repository.NotificationRepository;
import com.example.app.repository.UserRepository;
import com.example.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUserNotifications(
            @RequestParam("user_id") Long userId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<NotificationResponse> result = notificationService.getUserNotifications(userId);
        if (limit < result.size()) {
            result = result.subList(0, limit);
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUserNotificationsByPath(@PathVariable("user_id") Long userId) {
        List<NotificationResponse> result = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/users/{user_id}/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUserUnreadNotifications(@PathVariable("user_id") Long userId) {
        List<NotificationResponse> result = notificationService.getUserUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/users/{user_id}/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@PathVariable("user_id") Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("所有通知已标记为已读", null));
    }

    @PostMapping("/{notification_id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable("notification_id") Long notificationId,
            @RequestBody(required = false) Map<String, Object> body) {
        Long userId = null;
        if (body != null && body.containsKey("user_id")) {
            Object userIdObj = body.get("user_id");
            if (userIdObj instanceof Number) {
                userId = ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                userId = Long.parseLong((String) userIdObj);
            }
        }
        if (userId == null) {
            userId = getCurrentUserIdOrNull();
        }
        if (userId == null) {
            throw new BusinessException("请登录后操作");
        }
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("通知不存在"));
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作该通知");
        }
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success("通知已标记为已读", null));
    }

    @GetMapping("/users/{user_id}/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnreadCount(@PathVariable("user_id") Long userId) {
        int count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("未登录");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    private Long getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
