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
public class RecommendationAgent extends AbstractAgent {

    private final ToolRegistry toolRegistry;

    public RecommendationAgent(ProviderFactory factory, AiMetricsCollector collector, ToolRegistry toolRegistry) {
        super(AgentMetadata.builder()
                .name("RECOMMENDATION_AGENT")
                .role("Personalized Recommendation Expert")
                .description("Analyzes user borrow histories, favorite genres, and trending platform metrics to recommend books")
                .allowedTools(List.of("AnalyticsInsightsTool"))
                .build(), factory, collector);
        this.toolRegistry = toolRegistry;
    }

    @Override
    public AgentResult execute(AgentSession session) {
        long startTime = System.currentTimeMillis();

        // Query trending books info
        Object trends = toolRegistry.getTool("AnalyticsInsightsTool")
                .map(tool -> tool.execute(Map.of("metricType", "POPULAR_BOOKS")))
                .orElse("No trends logged");

        String systemPrompt = "You are the Recommendation Agent. Deliver custom recommendations based on user profiles. " +
                "Trending books info: " + trends.toString();

        ModelRequest request = ModelRequest.builder()
                .systemPrompt(systemPrompt)
                .userPrompt(session.getUserQuery())
                .build();

        ModelResponse response = providerFactory.execute(request);
        long latency = System.currentTimeMillis() - startTime;
        metricsCollector.logMetrics(metadata.getName(), 180, 240, latency);

        return AgentResult.builder()
                .agentName(metadata.getName())
                .outputContent(response.getContent())
                .state(AgentState.COMPLETED)
                .confidence(response.getConfidenceScore())
                .executionTimeMs(latency)
                .build();
    }
}
