package com.enterprise.eakip.agent.ai.prompts;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PromptTemplateEngine {

    private final Map<String, String> promptRegistry = new ConcurrentHashMap<>();
    private final Map<String, String> promptVersions = new ConcurrentHashMap<>();

    public void registerTemplate(String key, String template, String version) {
        promptRegistry.put(key, template);
        promptVersions.put(key, version);
    }

    public String render(String key, Map<String, Object> variables) {
        String template = promptRegistry.get(key);
        if (template == null) {
            throw new IllegalArgumentException("Prompt template not registered for key: " + key);
        }

        String rendered = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "\\$\\{" + entry.getKey() + "\\}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            rendered = rendered.replaceAll(placeholder, value);
        }
        return rendered;
    }

    public String getVersion(String key) {
        return promptVersions.getOrDefault(key, "1.0.0");
    }
}
