
package com.example.app.service;

import com.example.app.dto.response.NotificationResponse;
import com.example.app.entity.Notification;
import com.example.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MessageProducerService messageProducerService;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toNotificationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::toNotificationResponse)
                .toList();
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void createNotification(Long userId, String title, String content, String type, Long relatedId) {
        log.info("Creating notification asynchronously: userId={}, title={}", userId, title);
        messageProducerService.sendNotification(userId, title, content, type, relatedId);
    }

    private NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .relatedId(notification.getRelatedId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
