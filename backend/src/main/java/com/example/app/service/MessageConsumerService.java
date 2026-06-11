package com.example.app.service;

import com.example.app.config.RabbitMQConfig;
import com.example.app.dto.EmailMessage;
import com.example.app.dto.NotificationMessage;
import com.example.app.entity.Notification;
import com.example.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumerService {

    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(NotificationMessage message) {
        log.info("Received notification message: userId={}, title={}", message.getUserId(), message.getTitle());
        
        try {
            Notification notification = Notification.builder()
                    .userId(message.getUserId())
                    .title(message.getTitle())
                    .content(message.getContent())
                    .type(message.getType())
                    .relatedId(message.getRelatedId())
                    .isRead(false)
                    .build();
            
            notificationRepository.save(notification);
            log.info("Successfully saved notification for user: {}", message.getUserId());
        } catch (Exception e) {
            log.error("Failed to process notification message", e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmail(EmailMessage message) {
        log.info("Received email message: to={}, subject={}", message.getTo(), message.getSubject());
        
        try {
            sendEmail(message);
            log.info("Successfully sent email to: {}", message.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to: {}", message.getTo(), e);
            throw e;
        }
    }

    private void sendEmail(EmailMessage message) {
        log.info("=== 模拟发送邮件 ===");
        log.info("收件人: {}", message.getTo());
        log.info("主题: {}", message.getSubject());
        log.info("内容: {}", message.getContent());
        log.info("模板: {}", message.getTemplate());
        log.info("========================");
    }
}