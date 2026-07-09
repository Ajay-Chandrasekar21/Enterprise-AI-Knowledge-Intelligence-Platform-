package com.enterprise.eakip.agent.ai.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CommunicationEmailTool implements Tool {

    @Override
    public String getName() {
        return "CommunicationEmailTool";
    }

    @Override
    public String getDescription() {
        return "Dispatches mock email reminders regarding reservation holds queue updates";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("recipientEmail", "Target email address");
        params.put("subject", "The subject header string");
        params.put("body", "Email body content");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String to = (String) arguments.get("recipientEmail");
        String sub = (String) arguments.get("subject");
        
        log.info("Sending mock email to {}, subject: {}", to, sub);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "SENT");
        result.put("messageId", "MSG-" + System.currentTimeMillis());
        return result;
    }
}
