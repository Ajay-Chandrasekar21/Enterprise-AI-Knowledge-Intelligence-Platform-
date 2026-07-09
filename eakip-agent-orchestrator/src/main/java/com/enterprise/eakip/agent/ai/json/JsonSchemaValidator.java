package com.enterprise.eakip.agent.ai.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonSchemaValidator {

    private final ObjectMapper objectMapper;

    public boolean isValidJson(String jsonContent) {
        try {
            objectMapper.readTree(jsonContent);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JSON format detected: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateSchema(String jsonContent, String expectedKeysCsv) {
        try {
            JsonNode root = objectMapper.readTree(jsonContent);
            String[] keys = expectedKeysCsv.split(",");
            for (String key : keys) {
                if (!root.has(key.trim())) {
                    log.warn("JSON missing required property schema: {}", key);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
