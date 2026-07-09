package com.enterprise.eakip.agent.ai.mcp.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class McpServerRegistryTest {

    private McpServerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new McpServerRegistry();
    }

    @Test
    void registerAndGetServer_StoresConfigCorrectly() {
        // Arrange
        McpServerRegistry.McpServerConfig config = McpServerRegistry.McpServerConfig.builder()
                .serverName("GitHub")
                .endpointUrl("http://localhost:8081/mcp")
                .authType("API_KEY")
                .status("CONNECTED")
                .build();

        // Act
        registry.registerServer(config);
        McpServerRegistry.McpServerConfig retrieved = registry.getServer("GitHub");

        // Assert
        assertNotNull(retrieved);
        assertEquals("GitHub", retrieved.getServerName());
        assertEquals("CONNECTED", retrieved.getStatus());

        List<McpServerRegistry.McpServerConfig> all = registry.getAllServers();
        assertEquals(1, all.size());

        // Unregister
        registry.unregisterServer("GitHub");
        assertNull(registry.getServer("GitHub"));
    }
}
