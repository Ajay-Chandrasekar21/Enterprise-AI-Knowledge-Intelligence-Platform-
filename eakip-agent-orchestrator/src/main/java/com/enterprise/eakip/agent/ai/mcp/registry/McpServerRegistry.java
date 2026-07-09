package com.enterprise.eakip.agent.ai.mcp.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class McpServerRegistry {

    private final Map<String, McpServerConfig> registry = new ConcurrentHashMap<>();

    public void registerServer(McpServerConfig config) {
        registry.put(config.getServerName().toUpperCase(), config);
    }

    public void unregisterServer(String serverName) {
        registry.remove(serverName.toUpperCase());
    }

    public List<McpServerConfig> getAllServers() {
        return new ArrayList<>(registry.values());
    }

    public McpServerConfig getServer(String serverName) {
        return registry.get(serverName.toUpperCase());
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class McpServerConfig {
        private String serverName;
        private String endpointUrl;
        private String status; // CONNECTED, DISCONNECTED, DEGRADED
        private long latencyMs;
        private String authType; // OAUTH2, API_KEY, JWT
        private String apiKey;
    }
}
