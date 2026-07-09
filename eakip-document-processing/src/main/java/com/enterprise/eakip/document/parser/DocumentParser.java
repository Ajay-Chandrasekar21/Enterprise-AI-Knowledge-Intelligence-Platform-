package com.enterprise.eakip.document.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
public class DocumentParser {

    public String parse(InputStream inputStream) {
        log.info("Starting multi-format document parsing using Apache Tika AutoDetectParser");
        try {
            BodyContentHandler handler = new BodyContentHandler(-1); // No character limit
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            AutoDetectParser parser = new AutoDetectParser();

            parser.parse(inputStream, handler, metadata, context);
            log.info("Document parsed successfully. Content length: {}", handler.toString().length());
            
            return handler.toString();
        } catch (Exception e) {
            log.error("Parsing failed with exception", e);
            throw new RuntimeException("Failed to parse document content: " + e.getMessage(), e);
        }
    }
}
