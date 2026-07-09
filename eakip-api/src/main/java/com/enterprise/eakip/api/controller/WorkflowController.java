package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.agent.ai.workflow.WorkflowDefinition;
import com.enterprise.eakip.agent.ai.workflow.WorkflowDefinitionRepository;
import com.enterprise.eakip.agent.ai.workflow.WorkflowInstance;
import com.enterprise.eakip.agent.ai.workflow.WorkflowInstanceRepository;
import com.enterprise.eakip.agent.ai.workflow.AutonomousWorkflowExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
@Tag(name = "Autonomous Workflow Engine API", description = "Endpoints for defining, scheduling and executing distributed agent/tool workflows")
public class WorkflowController {

    private final WorkflowDefinitionRepository definitionRepository;
    private final WorkflowInstanceRepository instanceRepository;
    private final AutonomousWorkflowExecutor workflowExecutor;

    @GetMapping("/definitions")
    @Operation(summary = "List workflow templates definitions", description = "Returns active sequential, conditional or parallel templates")
    public ResponseEntity<ApiResponse<List<WorkflowDefinition>>> getDefinitions() {
        return ResponseEntity.ok(ApiResponse.success("Workflow definitions loaded", definitionRepository.findAll()));
    }

    @PostMapping("/definitions")
    @Operation(summary = "Create custom workflow template")
    public ResponseEntity<ApiResponse<WorkflowDefinition>> createDefinition(
            @RequestParam String name,
            @RequestParam String triggerType) {
            
        String nodesJson = "[{\"id\":\"node_1\",\"type\":\"TOOL\",\"name\":\"LibraryBorrowTool\"}," +
                "{\"id\":\"node_2\",\"type\":\"APPROVAL\",\"name\":\"Librarian Review\"}," +
                "{\"id\":\"node_3\",\"type\":\"NOTIFICATION\",\"name\":\"Send Receipt\"}]";

        WorkflowDefinition definition = WorkflowDefinition.builder()
                .name(name)
                .triggerType(triggerType)
                .nodesJson(nodesJson)
                .build();

        definition = definitionRepository.save(definition);
        return ResponseEntity.ok(ApiResponse.success("Workflow definition saved successfully", definition));
    }

    @PostMapping("/execute")
    @Operation(summary = "Execute workflow instance", description = "Spawns new execution instance, runs steps autonomously and registers checkpoints")
    public ResponseEntity<ApiResponse<WorkflowInstance>> executeWorkflow(@RequestParam UUID definitionId) {
        WorkflowInstance instance = workflowExecutor.startExecution(definitionId);
        return ResponseEntity.ok(ApiResponse.success("Workflow execution launched successfully", instance));
    }

    @PostMapping("/approve")
    @Operation(summary = "Approve pending step", description = "Resumes paused workflow executions from waiting points")
    public ResponseEntity<ApiResponse<String>> approveStep(@RequestParam UUID instanceId) {
        workflowExecutor.approveStep(instanceId);
        return ResponseEntity.ok(ApiResponse.success("Pending step approved. Workflow resumed.", instanceId.toString()));
    }

    @GetMapping("/instances")
    @Operation(summary = "List workflow active instances logs")
    public ResponseEntity<ApiResponse<List<WorkflowInstance>>> getInstances() {
        return ResponseEntity.ok(ApiResponse.success("Workflow instances loaded", instanceRepository.findAll()));
    }
}
