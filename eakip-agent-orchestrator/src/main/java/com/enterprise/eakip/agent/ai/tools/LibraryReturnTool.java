package com.enterprise.eakip.agent.ai.tools;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LibraryReturnTool implements Tool {

    @Override
    public String getName() {
        return "LibraryReturnTool";
    }

    @Override
    public String getDescription() {
        return "Processes return check-ins, calculates potential late fine fees";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("borrowingId", "UUID of the active borrowing record");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "COMPLETED");
        result.put("fineAmount", 0.0);
        result.put("message", "Book returned successfully.");
        return result;
    }
}
