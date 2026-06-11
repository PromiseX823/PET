
package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.response.NotificationResponse;
import com.example.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

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
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable("notification_id") Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success("通知已标记为已读", null));
    }

    @GetMapping("/users/{user_id}/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnreadCount(@PathVariable("user_id") Long userId) {
        int count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }
}
