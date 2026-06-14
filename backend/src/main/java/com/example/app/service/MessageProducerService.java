package com.example.app.service;

import com.example.app.config.RabbitMQConfig;
import com.example.app.entity.Notification;
import com.example.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ消息生产者服务
 * 将耗时操作转为异步消息队列处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerService {

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    private final NotificationRepository notificationRepository;

    /**
     * 发送通知消息（异步）
     * 如果RabbitMQ可用则通过消息队列发送，否则直接保存
     */
    public void sendNotification(Long userId, String title, String content, String type, Long relatedId) {
        NotificationMessage message = NotificationMessage.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .type(type)
                .relatedId(relatedId)
                .build();

        if (rabbitTemplate != null) {
            log.info("通过RabbitMQ发送通知消息: userId={}, title={}", userId, title);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PET_EXCHANGE,
                    RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                    message
            );
        } else {
            log.info("RabbitMQ不可用，直接保存通知: userId={}, title={}", userId, title);
            saveNotificationDirectly(message);
        }
    }

    /**
     * 直接保存通知（当RabbitMQ不可用时的备用方案）
     */
    private void saveNotificationDirectly(NotificationMessage message) {
        Notification notification = Notification.builder()
                .userId(message.getUserId())
                .title(message.getTitle())
                .content(message.getContent())
                .type(message.getType())
                .relatedId(message.getRelatedId())
                .isRead(false)
                .build();
        
        notificationRepository.save(notification);
    }

    /**
     * 发送邮件消息（异步）
     */
    public void sendEmail(String to, String subject, String content) {
        if (rabbitTemplate != null) {
            MessageConsumerService.EmailMessage message = MessageConsumerService.EmailMessage.builder()
                    .to(to)
                    .subject(subject)
                    .content(content)
                    .build();
            
            log.info("通过RabbitMQ发送邮件消息: to={}, subject={}", to, subject);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PET_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    message
            );
        } else {
            log.warn("RabbitMQ不可用，无法发送邮件: to={}", to);
        }
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificationMessage {
        private Long userId;
        private String title;
        private String content;
        private String type;
        private Long relatedId;
    }
}