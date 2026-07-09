package com.enterprise.eakip.agent.ai.tools;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ToolRegistry {

    private final List<Tool> tools;
    private final Map<String, Tool> registry = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        for (Tool tool : tools) {
            register(tool);
        }
    }

    public void register(Tool tool) {
        registry.put(tool.getName().toUpperCase(), tool);
    }

    public Optional<Tool> getTool(String name) {
        return Optional.ofNullable(registry.get(name.toUpperCase()));
    }

    public Collection<Tool> getAllTools() {
        return registry.values();
    }
}
