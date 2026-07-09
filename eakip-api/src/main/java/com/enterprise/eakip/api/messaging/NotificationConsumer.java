package com.enterprise.eakip.api.messaging;

import com.enterprise.eakip.api.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.NOTIFICATION_QUEUE)
    public void consumeNotification(Object message) {
        log.info("Received notification task: {}", message);
        // Stub for future notification dispatch (WebSocket, Email, SMS)
    }
}
