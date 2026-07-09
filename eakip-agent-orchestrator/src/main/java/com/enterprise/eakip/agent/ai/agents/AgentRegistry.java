package com.enterprise.eakip.agent.ai.agents;

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
public class AgentRegistry {

    private final List<Agent> agents;
    private final Map<String, Agent> registry = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        for (Agent agent : agents) {
            register(agent);
        }
    }

    public void register(Agent agent) {
        registry.put(agent.getMetadata().getName().toUpperCase(), agent);
    }

    public Optional<Agent> getAgent(String name) {
        return Optional.ofNullable(registry.get(name.toUpperCase()));
    }

    public Collection<Agent> getAllAgents() {
        return registry.values();
    }
}
