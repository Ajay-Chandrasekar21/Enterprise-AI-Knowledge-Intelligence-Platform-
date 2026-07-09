package com.enterprise.eakip.agent.ai.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CommunicationWebhookTool implements Tool {

    @Override
    public String getName() {
        return "CommunicationWebhookTool";
    }

    @Override
    public String getDescription() {
        return "Sends callback JSON webhooks notify events to external enterprise endpoints";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("url", "Webhook callback URL target");
        params.put("payload", "JSON serialized request body context");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String url = (String) arguments.get("url");
        log.info("Mocking Webhook dispatch callback URL: {}", url);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "SUCCESS");
        result.put("responseCode", 200);
        return result;
    }
}
