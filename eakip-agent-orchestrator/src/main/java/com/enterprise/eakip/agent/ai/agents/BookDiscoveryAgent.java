package com.enterprise.eakip.agent.ai.agents;

import com.enterprise.eakip.agent.ai.core.ModelRequest;
import com.enterprise.eakip.agent.ai.core.ModelResponse;
import com.enterprise.eakip.agent.ai.core.ProviderFactory;
import com.enterprise.eakip.agent.ai.observability.AiMetricsCollector;
import com.enterprise.eakip.agent.ai.runtime.AgentResult;
import com.enterprise.eakip.agent.ai.runtime.AgentSession;
import com.enterprise.eakip.agent.ai.runtime.AgentState;
import com.enterprise.eakip.agent.ai.tools.ToolRegistry;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BookDiscoveryAgent extends AbstractAgent {

    private final ToolRegistry toolRegistry;

    public BookDiscoveryAgent(ProviderFactory factory, AiMetricsCollector collector, ToolRegistry toolRegistry) {
        super(AgentMetadata.builder()
                .name("BOOK_DISCOVERY_AGENT")
                .role("Catalog Search Specialist")
                .description("Handles book discovery, natural language book searching, and availability lookups")
                .allowedTools(List.of("BookCatalogSearchTool"))
                .build(), factory, collector);
        this.toolRegistry = toolRegistry;
    }

    @Override
    public AgentResult execute(AgentSession session) {
        long startTime = System.currentTimeMillis();
        
        // 1. Invoke Book Search Tool to fetch matching catalog records
        Object searchResults = toolRegistry.getTool("BookCatalogSearchTool")
                .map(tool -> tool.execute(Map.of("title", session.getUserQuery())))
                .orElse("No books found matching search query");

        // 2. Format system/developer instructions with tool contexts
        String systemPrompt = "You are the Book Discovery Agent. Help the user discover books and detail why recommendations are selected. " +
                "Direct catalog matches context: " + searchResults.toString();

        ModelRequest request = ModelRequest.builder()
                .systemPrompt(systemPrompt)
                .userPrompt(session.getUserQuery())
                .build();

        ModelResponse response = providerFactory.execute(request);
        long latency = System.currentTimeMillis() - startTime;
        metricsCollector.logMetrics(metadata.getName(), 150, 200, latency);

        return AgentResult.builder()
                .agentName(metadata.getName())
                .outputContent(response.getContent())
                .state(AgentState.COMPLETED)
                .confidence(response.getConfidenceScore())
                .executionTimeMs(latency)
                .build();
    }
}
