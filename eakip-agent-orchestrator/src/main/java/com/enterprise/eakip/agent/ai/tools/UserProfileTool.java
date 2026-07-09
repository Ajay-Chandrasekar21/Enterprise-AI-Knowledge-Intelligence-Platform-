package com.enterprise.eakip.agent.ai.tools;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserProfileTool implements Tool {

    @Override
    public String getName() {
        return "UserProfileTool";
    }

    @Override
    public String getDescription() {
        return "Retrieves user interests tags, favorite categories and registered department identifiers";
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
        result.put("role", "STUDENT");
        result.put("department", "Computer Science");
        result.put("interests", new String[]{"backend development", "compilers design", "algorithms"});
        return result;
    }
}
