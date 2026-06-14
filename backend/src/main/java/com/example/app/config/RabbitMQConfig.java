package com.example.app.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    // 队列名称
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String EMAIL_QUEUE = "email.queue";
    
    // 交换机名称
    public static final String PET_EXCHANGE = "pet.exchange";
    
    // 路由键
    public static final String NOTIFICATION_ROUTING_KEY = "notification";
    public static final String EMAIL_ROUTING_KEY = "email";

    /**
     * 通知队列
     */
    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    /**
     * 邮件队列
     */
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    /**
     * 交换机
     */
    @Bean
    public TopicExchange petExchange() {
        return new TopicExchange(PET_EXCHANGE);
    }

    /**
     * 绑定通知队列到交换机
     */
    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange petExchange) {
        return BindingBuilder.bind(notificationQueue).to(petExchange).with(NOTIFICATION_ROUTING_KEY);
    }

    /**
     * 绑定邮件队列到交换机
     */
    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange petExchange) {
        return BindingBuilder.bind(emailQueue).to(petExchange).with(EMAIL_ROUTING_KEY);
    }

    /**
     * JSON消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}