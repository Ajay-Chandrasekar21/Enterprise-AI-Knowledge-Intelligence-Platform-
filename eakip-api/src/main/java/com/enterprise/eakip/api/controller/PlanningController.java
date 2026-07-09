package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.agent.ai.planning.AgentPlanner;
import com.enterprise.eakip.agent.ai.planning.TaskGraph;
import com.enterprise.eakip.agent.ai.reasoning.ReasoningEngine;
import com.enterprise.eakip.agent.ai.reflection.ReflectionEngine;
import com.enterprise.eakip.agent.ai.consensus.ConsensusEngine;
import com.enterprise.eakip.agent.ai.runtime.AgentResult;
import com.enterprise.eakip.agent.ai.runtime.AgentState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/planning")
@RequiredArgsConstructor
@Tag(name = "Agent Planning & Reasoning API", description = "Endpoints for dynamic execution plans, chain of thought reasoning, and response consensus")
public class PlanningController {

    private final AgentPlanner planner;
    private final ReasoningEngine reasoningEngine;
    private final ReflectionEngine reflectionEngine;
    private final ConsensusEngine consensusEngine;

    @GetMapping("/graph")
    @Operation(summary = "Generate task execution graph", description = "Analyzes query intent and plans sequential, parallel or conditional task dependencies")
    public ResponseEntity<ApiResponse<TaskGraph>> getTaskGraph(@RequestParam String query) {
        TaskGraph graph = planner.generatePlan(query);
        return ResponseEntity.ok(ApiResponse.success("Task Graph generated successfully", graph));
    }

    @PostMapping("/reason")
    @Operation(summary = "Execute reasoning chain", description = "Triggers Chain-of-Thought planning parameters to decompose instructions")
    public ResponseEntity<ApiResponse<ReasoningEngine.ReasoningResult>> executeReasoning(@RequestBody String query) {
        ReasoningEngine.ReasoningResult result = reasoningEngine.executeReasoning(query);
        return ResponseEntity.ok(ApiResponse.success("Reasoning chain executed", result));
    }

    @PostMapping("/reflect")
    @Operation(summary = "Self-evaluate response content", description = "Runs reflection validation checking accuracy, completeness and consistency scores")
    public ResponseEntity<ApiResponse<ReflectionEngine.ReflectionResult>> reflect(@RequestBody String content) {
        ReflectionEngine.ReflectionResult result = reflectionEngine.evaluate(content);
        return ResponseEntity.ok(ApiResponse.success("Self-reflection completed", result));
    }

    @PostMapping("/consensus")
    @Operation(summary = "Resolve multi-agent responses consensus", description = "Ranks and merges outputs from different agents to formulate a unified response")
    public ResponseEntity<ApiResponse<String>> resolveConsensus(@RequestBody List<String> agentOutputs) {
        List<AgentResult> results = new ArrayList<>();
        for (int i = 0; i < agentOutputs.size(); i++) {
            results.add(AgentResult.builder()
                    .agentName("Agent_" + i)
                    .outputContent(agentOutputs.get(i))
                    .state(AgentState.COMPLETED)
                    .confidence(0.85 + (i * 0.05))
                    .build());
        }
        String merged = consensusEngine.resolveConsensus(results);
        return ResponseEntity.ok(ApiResponse.success("Consensus merged response", merged));
    }
}
