package com.enterprise.eakip.agent.ai.tools;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LibraryReservationTool implements Tool {

    @Override
    public String getName() {
        return "LibraryReservationTool";
    }

    @Override
    public String getDescription() {
        return "Registers reservations holds on currently borrowed catalog titles";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("bookId", "UUID of the target book");
        params.put("userId", "UUID of the user");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "QUEUED");
        result.put("queuePosition", 2);
        result.put("message", "Reservation created successfully. Queue position: 2");
        return result;
    }
}
