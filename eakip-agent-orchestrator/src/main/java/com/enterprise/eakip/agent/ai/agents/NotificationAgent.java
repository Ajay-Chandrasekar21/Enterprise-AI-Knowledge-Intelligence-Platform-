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
public class NotificationAgent extends AbstractAgent {

    private final ToolRegistry toolRegistry;

    public NotificationAgent(ProviderFactory factory, AiMetricsCollector collector, ToolRegistry toolRegistry) {
        super(AgentMetadata.builder()
                .name("NOTIFICATION_AGENT")
                .role("Intelligent Notification Manager")
                .description("Automates dispatching borrow due warnings, reservation availability updates, and reading streak alerts")
                .allowedTools(List.of("LibraryNotificationTool"))
                .build(), factory, collector);
        this.toolRegistry = toolRegistry;
    }

    @Override
    public AgentResult execute(AgentSession session) {
        long startTime = System.currentTimeMillis();

        String alertMsg = "Reminder check: " + session.getUserQuery();
        Object dispatchStatus = toolRegistry.getTool("LibraryNotificationTool")
                .map(tool -> tool.execute(Map.of("recipient", "SystemUser", "message", alertMsg)))
                .orElse("No notification engine loaded");

        String systemPrompt = "You are the Notification Agent. Draft clean alerts, borrow reminders, and return notifications. " +
                "Push log receipt status: " + dispatchStatus.toString();

        ModelRequest request = ModelRequest.builder()
                .systemPrompt(systemPrompt)
                .userPrompt(session.getUserQuery())
                .build();

        ModelResponse response = providerFactory.execute(request);
        long latency = System.currentTimeMillis() - startTime;
        metricsCollector.logMetrics(metadata.getName(), 140, 180, latency);

        return AgentResult.builder()
                .agentName(metadata.getName())
                .outputContent(response.getContent())
                .state(AgentState.COMPLETED)
                .confidence(response.getConfidenceScore())
                .executionTimeMs(latency)
                .build();
    }
}
