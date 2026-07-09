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

import java.util.List;
import java.util.Map;

@Component
public class SemanticSearchAgent extends AbstractAgent {

    private final ToolRegistry toolRegistry;

    public SemanticSearchAgent(ProviderFactory factory, AiMetricsCollector collector, ToolRegistry toolRegistry) {
        super(AgentMetadata.builder()
                .name("SEMANTIC_SEARCH_AGENT")
                .role("Document Semantic Search Expert")
                .description("Queries document parsed chunks using vector embeddings similarity lookups")
                .allowedTools(List.of("RAGDocumentSearchTool"))
                .build(), factory, collector);
        this.toolRegistry = toolRegistry;
    }

    @Override
    public AgentResult execute(AgentSession session) {
        long startTime = System.currentTimeMillis();

        // 1. Invoke vector document search tool
        Object ragChunks = toolRegistry.getTool("RAGDocumentSearchTool")
                .map(tool -> tool.execute(Map.of("query", session.getUserQuery())))
                .orElse("No semantic matches retrieved");

        // 2. Feed text snippets context to developer prompt
        String systemPrompt = "You are the Semantic Search Agent. Provide natural language queries ranking over RAG document index. " +
                "Matching vector search chunks context: " + ragChunks.toString();

        ModelRequest request = ModelRequest.builder()
                .systemPrompt(systemPrompt)
                .userPrompt(session.getUserQuery())
                .build();

        ModelResponse response = providerFactory.execute(request);
        long latency = System.currentTimeMillis() - startTime;
        metricsCollector.logMetrics(metadata.getName(), 200, 300, latency);

        return AgentResult.builder()
                .agentName(metadata.getName())
                .outputContent(response.getContent())
                .state(AgentState.COMPLETED)
                .confidence(response.getConfidenceScore())
                .executionTimeMs(latency)
                .build();
    }
}
