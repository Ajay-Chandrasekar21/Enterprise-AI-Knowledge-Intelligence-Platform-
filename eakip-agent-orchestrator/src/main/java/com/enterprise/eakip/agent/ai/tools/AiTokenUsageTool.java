package com.enterprise.eakip.agent.ai.tools;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiTokenUsageTool implements Tool {

    @Override
    public String getName() {
        return "AiTokenUsageTool";
    }

    @Override
    public String getDescription() {
        return "Calculates approximate token usage length counts for text logs";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("text", "Target string to analyze");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String text = (String) arguments.get("text");
        int length = text != null ? text.length() / 4 : 0; // rough character split approximation
        
        Map<String, Object> result = new HashMap<>();
        result.put("tokenCount", length);
        result.put("status", "STABLE");
        return result;
    }
}
