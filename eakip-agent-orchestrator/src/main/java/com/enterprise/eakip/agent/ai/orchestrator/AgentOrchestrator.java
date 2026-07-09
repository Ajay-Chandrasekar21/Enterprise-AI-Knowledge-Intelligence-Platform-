package com.enterprise.eakip.agent.ai.orchestrator;

import com.enterprise.eakip.agent.ai.executor.WorkflowExecutor;
import com.enterprise.eakip.agent.ai.planner.ExecutionPlan;
import com.enterprise.eakip.agent.ai.planner.ExecutionPlanner;
import com.enterprise.eakip.agent.ai.runtime.AgentResult;
import com.enterprise.eakip.agent.ai.runtime.AgentSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final ExecutionPlanner planner;
    private final WorkflowExecutor executor;

    public OrchestrationResult processQuery(UUID userId, String query) {
        log.info("Orchestrator received user query for user: {}", userId);
        
        // 1. Build execution plan
        ExecutionPlan plan = planner.plan(query);

        // 2. Initialize session variables
        String sessionId = UUID.randomUUID().toString();
        AgentSession session = AgentSession.builder()
                .sessionId(sessionId)
                .userId(userId)
                .userQuery(query)
                .build();

        // 3. Execute agents workflow
        List<AgentResult> results = executor.execute(plan, session);

        // 4. Combine responses into unified final context
        String combinedOutput = results.stream()
                .map(res -> "[" + res.getAgentName() + "]: " + res.getOutputContent())
                .collect(Collectors.joining("\n\n"));

        double averageConfidence = results.stream()
                .mapToDouble(res -> res.getConfidence() != null ? res.getConfidence() : 0.0)
                .average()
                .orElse(1.0);

        return OrchestrationResult.builder()
                .sessionId(sessionId)
                .planId(plan.getPlanId())
                .combinedResponse(combinedOutput)
                .confidenceScore(averageConfidence)
                .build();
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class OrchestrationResult {
        private String sessionId;
        private String planId;
        private String combinedResponse;
        private Double confidenceScore;
    }
}
