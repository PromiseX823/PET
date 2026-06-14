package com.example.app.service;

import com.example.app.config.RabbitMQConfig;
import com.example.app.entity.Notification;
import com.example.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ消息消费者
 * 监听队列并处理消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumerService {

    private final NotificationRepository notificationRepository;

    /**
     * 监听通知队列，处理通知消息
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(MessageProducerService.NotificationMessage message) {
        log.info("收到通知消息: userId={}, title={}", message.getUserId(), message.getTitle());
        
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
            log.info("通知已保存: userId={}, title={}", message.getUserId(), message.getTitle());
        } catch (Exception e) {
            log.error("处理通知消息失败: {}", e.getMessage(), e);
            // 可以选择重新入队或记录到失败表
        }
    }

    /**
     * 监听邮件队列，处理邮件发送
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmail(EmailMessage message) {
        log.info("收到邮件消息: to={}, subject={}", message.getTo(), message.getSubject());
        
        try {
            // 这里可以集成真实的邮件发送服务
            // 例如使用JavaMailSender或第三方邮件API
            log.info("邮件发送成功: to={}", message.getTo());
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 邮件消息DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EmailMessage {
        private String to;
        private String subject;
        private String content;
    }
}