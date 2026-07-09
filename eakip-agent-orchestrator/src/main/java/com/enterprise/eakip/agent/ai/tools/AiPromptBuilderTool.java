package com.enterprise.eakip.agent.ai.tools;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiPromptBuilderTool implements Tool {

    @Override
    public String getName() {
        return "AiPromptBuilderTool";
    }

    @Override
    public String getDescription() {
        return "Injects context variables into preset template files, formulating developer system prompts";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("templateName", "Target prompt library template key");
        params.put("variables", "Key-value pair map parameter string");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String template = (String) arguments.get("templateName");
        Map<String, Object> result = new HashMap<>();
        result.put("renderedPrompt", "System role prompted using template: " + template);
        result.put("tokenEstimate", 85);
        return result;
    }
}
