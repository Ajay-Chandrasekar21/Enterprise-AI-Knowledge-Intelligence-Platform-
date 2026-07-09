package com.enterprise.eakip.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "eakip.exchange";
    
    public static final String DOCUMENT_PROCESSING_QUEUE = "document.processing.queue";
    public static final String DOCUMENT_PROCESSING_ROUTING_KEY = "document.processing.routingkey";

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.routingkey";

    @Bean
    public TopicExchange eakipExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue documentProcessingQueue() {
        return new Queue(DOCUMENT_PROCESSING_QUEUE, true);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding documentProcessingBinding() {
        return BindingBuilder
                .bind(documentProcessingQueue())
                .to(eakipExchange())
                .with(DOCUMENT_PROCESSING_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(eakipExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
