package com.example.app.service;

import com.example.app.config.RabbitMQConfig;
import com.example.app.dto.EmailMessage;
import com.example.app.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerService {

    private final RabbitTemplate rabbitTemplate;

    public void sendNotification(Long userId, String title, String content, String type, Long relatedId) {
        NotificationMessage message = NotificationMessage.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .type(type)
                .relatedId(relatedId)
                .createdAt(LocalDateTime.now())
                .build();

        log.info("Sending notification to queue: userId={}, title={}", userId, title);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                message
        );
        log.info("Notification sent successfully");
    }

    public void sendEmail(String to, String subject, String content) {
        EmailMessage message = EmailMessage.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .build();

        log.info("Sending email to queue: to={}, subject={}", to, subject);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                message
        );
        log.info("Email sent successfully");
    }

    public void sendEmailWithTemplate(String to, String subject, String template) {
        EmailMessage message = EmailMessage.builder()
                .to(to)
                .subject(subject)
                .template(template)
                .build();

        log.info("Sending email template to queue: to={}, template={}", to, template);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                message
        );
        log.info("Email template sent successfully");
    }
}