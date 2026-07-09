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
public class LibraryAnalyticsAgent extends AbstractAgent {

    private final ToolRegistry toolRegistry;

    public LibraryAnalyticsAgent(ProviderFactory factory, AiMetricsCollector collector, ToolRegistry toolRegistry) {
        super(AgentMetadata.builder()
                .name("ANALYTICS_AGENT")
                .role("Data Analytics Specialist")
                .description("Generates usage statistics, identifies borrow trends, and compiles summaries for reports")
                .allowedTools(List.of("AnalyticsInsightsTool"))
                .build(), factory, collector);
        this.toolRegistry = toolRegistry;
    }

    @Override
    public AgentResult execute(AgentSession session) {
        long startTime = System.currentTimeMillis();

        Object borrowTrends = toolRegistry.getTool("AnalyticsInsightsTool")
                .map(tool -> tool.execute(Map.of("metricType", "BORROW_TRENDS")))
                .orElse("No borrow logs statistics");

        String systemPrompt = "You are the Library Analytics Agent. Compile borrow frequencies, category distributions, and generate report summaries. " +
                "Borrow trends dataset: " + borrowTrends.toString();

        ModelRequest request = ModelRequest.builder()
                .systemPrompt(systemPrompt)
                .userPrompt(session.getUserQuery())
                .build();

        ModelResponse response = providerFactory.execute(request);
        long latency = System.currentTimeMillis() - startTime;
        metricsCollector.logMetrics(metadata.getName(), 180, 260, latency);

        return AgentResult.builder()
                .agentName(metadata.getName())
                .outputContent(response.getContent())
                .state(AgentState.COMPLETED)
                .confidence(response.getConfidenceScore())
                .executionTimeMs(latency)
                .build();
    }
}
