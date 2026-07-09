package com.enterprise.eakip.api.messaging;

import com.enterprise.eakip.api.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class DocumentProcessingConsumer {

    private static final Logger log = LoggerFactory.getLogger(DocumentProcessingConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.DOCUMENT_PROCESSING_QUEUE)
    public void consumeDocumentJob(Object message) {
        log.info("Received document processing job: {}", message);
        // Stub for future file parser execution (Apache Tika/PDFBox/OCR)
    }
}
