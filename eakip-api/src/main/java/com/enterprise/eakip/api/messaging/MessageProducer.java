package com.enterprise.eakip.api.messaging;

import com.enterprise.eakip.api.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);
    
    private final RabbitTemplate rabbitTemplate;

    public <T> void sendDocumentJob(T message) {
        log.info("Publishing document processing task: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME, 
                RabbitMqConfig.DOCUMENT_PROCESSING_ROUTING_KEY, 
                message
        );
    }

    public <T> void sendNotification(T message) {
        log.info("Publishing notification event: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME, 
                RabbitMqConfig.NOTIFICATION_ROUTING_KEY, 
                message
        );
    }
}
