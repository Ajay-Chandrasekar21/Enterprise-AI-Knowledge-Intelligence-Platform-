package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.agent.ai.observability.AiMetricsCollector;
import com.enterprise.eakip.agent.ai.orchestrator.AgentOrchestrator;
import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.security.service.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Agent Runtime Orchestrator API", description = "Endpoints for query orchestration and LLM telemetry metrics")
public class AgentOrchestratorController {

    private final AgentOrchestrator orchestrator;
    private final AiMetricsCollector metricsCollector;

    @PostMapping("/query")
    @Operation(summary = "Submit query to Multi-Agent Orchestrator", description = "Analyzes query intent, drafts workflow execution plan, runs agents, and merges responses")
    public ResponseEntity<ApiResponse<AgentOrchestrator.OrchestrationResult>> submitQuery(
            @RequestBody QueryRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
            
        AgentOrchestrator.OrchestrationResult result = orchestrator.processQuery(principal.getId(), request.getQuery());
        return ResponseEntity.ok(ApiResponse.success("Query orchestrated successfully", result));
    }

    @GetMapping("/metrics")
    @Operation(summary = "Retrieve AI runtime performance statistics", description = "Queries token logs, latency registers, and error counters")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTelemetry() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalTokens", metricsCollector.getTotalTokens());
        data.put("requestsCount", metricsCollector.getRequestsCount());
        data.put("failuresCount", metricsCollector.getFailuresCount());
        data.put("averageLatencyMs", metricsCollector.getAverageLatencyMs());
        
        return ResponseEntity.ok(ApiResponse.success("Telemetry logs loaded", data));
    }

    @Getter
    @Setter
    public static class QueryRequest {
        private String query;
    }
}
