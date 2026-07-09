package com.enterprise.eakip.agent.ai.agents;

import com.enterprise.eakip.agent.ai.core.ModelRequest;
import com.enterprise.eakip.agent.ai.core.ModelResponse;
import com.enterprise.eakip.agent.ai.core.ProviderFactory;
import com.enterprise.eakip.agent.ai.observability.AiMetricsCollector;
import com.enterprise.eakip.agent.ai.runtime.AgentResult;
import com.enterprise.eakip.agent.ai.runtime.AgentSession;
import com.enterprise.eakip.agent.ai.runtime.AgentState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractAgent implements Agent {

    protected final AgentMetadata metadata;
    protected final ProviderFactory providerFactory;
    protected final AiMetricsCollector metricsCollector;

    @Override
    public AgentResult execute(AgentSession session) {
        long startTime = System.currentTimeMillis();
        try {
            ModelRequest request = ModelRequest.builder()
                    .systemPrompt("You are " + metadata.getName() + ", role: " + metadata.getRole())
                    .userPrompt(session.getUserQuery())
                    .build();

            ModelResponse response = providerFactory.execute(request);
            long latency = System.currentTimeMillis() - startTime;
            metricsCollector.logMetrics(response.getContent(), 0, 0, latency);

            return AgentResult.builder()
                    .agentName(metadata.getName())
                    .outputContent(response.getContent())
                    .state(AgentState.COMPLETED)
                    .confidence(response.getConfidenceScore())
                    .executionTimeMs(latency)
                    .build();
        } catch (Exception e) {
            metricsCollector.logFailure(metadata.getName(), e.getMessage());
            return AgentResult.builder()
                    .agentName(metadata.getName())
                    .state(AgentState.FAILED)
                    .errorMessage(e.getMessage())
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
}
