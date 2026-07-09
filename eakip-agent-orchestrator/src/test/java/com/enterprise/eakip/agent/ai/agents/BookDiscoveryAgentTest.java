package com.enterprise.eakip.agent.ai.agents;

import com.enterprise.eakip.agent.ai.core.ModelResponse;
import com.enterprise.eakip.agent.ai.core.ProviderFactory;
import com.enterprise.eakip.agent.ai.observability.AiMetricsCollector;
import com.enterprise.eakip.agent.ai.runtime.AgentResult;
import com.enterprise.eakip.agent.ai.runtime.AgentSession;
import com.enterprise.eakip.agent.ai.runtime.AgentState;
import com.enterprise.eakip.agent.ai.tools.Tool;
import com.enterprise.eakip.agent.ai.tools.ToolRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookDiscoveryAgentTest {

    @Mock
    private ProviderFactory providerFactory;

    @Mock
    private AiMetricsCollector metricsCollector;

    @Mock
    private ToolRegistry toolRegistry;

    @Mock
    private Tool mockTool;

    private BookDiscoveryAgent agent;

    @BeforeEach
    void setUp() {
        agent = new BookDiscoveryAgent(providerFactory, metricsCollector, toolRegistry);
    }

    @Test
    void testAgentMetadata() {
        assertNotNull(agent.getMetadata());
        assertEquals("BOOK_DISCOVERY_AGENT", agent.getMetadata().getName());
        assertEquals("Catalog Search Specialist", agent.getMetadata().getRole());
    }

    @Test
    void execute_CallsToolAndProvider_ReturnsResult() {
        // Arrange
        AgentSession session = AgentSession.builder()
                .sessionId("test-sess")
                .userId(UUID.randomUUID())
                .userQuery("Java programming")
                .build();

        when(toolRegistry.getTool("BookCatalogSearchTool")).thenReturn(Optional.of(mockTool));
        when(mockTool.execute(any())).thenReturn("Clean Code book context");

        ModelResponse mockResponse = ModelResponse.builder()
                .content("Mock discovery response matching Java programming")
                .confidenceScore(0.96)
                .build();
        when(providerFactory.execute(any())).thenReturn(mockResponse);

        // Act
        AgentResult result = agent.execute(session);

        // Assert
        assertNotNull(result);
        assertEquals(AgentState.COMPLETED, result.getState());
        assertEquals("BOOK_DISCOVERY_AGENT", result.getAgentName());
        assertEquals("Mock discovery response matching Java programming", result.getOutputContent());
        assertEquals(0.96, result.getConfidence());
    }
}
