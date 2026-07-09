package com.enterprise.eakip.agent.ai.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class NotificationTool implements Tool {

    @Override
    public String getName() {
        return "LibraryNotificationTool";
    }

    @Override
    public String getDescription() {
        return "Triggers notifications logs push alert messages to user devices";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("recipient", "Target username or ID");
        params.put("message", "The text reminder body context");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String recipient = (String) arguments.get("recipient");
        String message = (String) arguments.get("message");
        
        log.info("Mock Notification Triggered - Recipient: {}, Msg: {}", recipient, message);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "DISPATCHED");
        result.put("recipient", recipient);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
