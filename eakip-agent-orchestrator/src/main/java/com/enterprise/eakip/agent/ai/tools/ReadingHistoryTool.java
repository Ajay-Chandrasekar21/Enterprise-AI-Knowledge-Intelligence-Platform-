package com.enterprise.eakip.agent.ai.tools;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReadingHistoryTool implements Tool {

    @Override
    public String getName() {
        return "ReadingHistoryTool";
    }

    @Override
    public String getDescription() {
        return "Exposes completed reading lists and loan durations frequencies per user";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", "UUID of the user");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        Map<String, Object> result = new HashMap<>();
        result.put("booksCompletedCount", 12);
        result.put("averageVelocityPagesPerMin", 1.25);
        return result;
    }
}
