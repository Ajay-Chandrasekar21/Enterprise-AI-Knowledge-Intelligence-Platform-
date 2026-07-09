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
public class ReadingCoachAgent extends AbstractAgent {

    private final ToolRegistry toolRegistry;

    public ReadingCoachAgent(ProviderFactory factory, AiMetricsCollector collector, ToolRegistry toolRegistry) {
        super(AgentMetadata.builder()
                .name("READING_COACH_AGENT")
                .role("Personalized Reading Mentor")
                .description("Monitors reading milestones, progress speeds, completion deadlines and designs habits goals")
                .allowedTools(List.of("DatabaseMetadataTool"))
                .build(), factory, collector);
        this.toolRegistry = toolRegistry;
    }

    @Override
    public AgentResult execute(AgentSession session) {
        long startTime = System.currentTimeMillis();

        Object userMetrics = toolRegistry.getTool("DatabaseMetadataTool")
                .map(tool -> tool.execute(Map.of("table", "users")))
                .orElse("No user sessions logged");

        String systemPrompt = "You are the Reading Coach Agent. Inspire users, review goals milestones and calculate completion durations. " +
                "User metadata context: " + userMetrics.toString();

        ModelRequest request = ModelRequest.builder()
                .systemPrompt(systemPrompt)
                .userPrompt(session.getUserQuery())
                .build();

        ModelResponse response = providerFactory.execute(request);
        long latency = System.currentTimeMillis() - startTime;
        metricsCollector.logMetrics(metadata.getName(), 160, 220, latency);

        return AgentResult.builder()
                .agentName(metadata.getName())
                .outputContent(response.getContent())
                .state(AgentState.COMPLETED)
                .confidence(response.getConfidenceScore())
                .executionTimeMs(latency)
                .build();
    }
}
