package com.enterprise.eakip.agent.ai.workflow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutonomousWorkflowExecutor {

    private final WorkflowInstanceRepository instanceRepository;
    private final WorkflowDefinitionRepository definitionRepository;

    @Transactional
    public WorkflowInstance startExecution(UUID definitionId) {
        log.info("Starting autonomous workflow execution. DefID={}", definitionId);
        WorkflowDefinition definition = definitionRepository.findById(definitionId)
                .orElseThrow(() -> new IllegalArgumentException("No workflow schema matches: " + definitionId));

        WorkflowInstance instance = WorkflowInstance.builder()
                .definition(definition)
                .currentNodeId("node_1")
                .status("RUNNING")
                .variablesJson("{}")
                .build();
        
        instance = instanceRepository.save(instance);
        executeNext(instance);
        return instance;
    }

    @Transactional
    public void approveStep(UUID instanceId) {
        log.info("Human approval recorded for workflow instance: {}", instanceId);
        WorkflowInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("No instance matches: " + instanceId));

        if ("WAITING_APPROVAL".equalsIgnoreCase(instance.getStatus())) {
            instance.setStatus("RUNNING");
            instance.setCurrentNodeId("node_3"); // proceed to next step
            instanceRepository.save(instance);
            executeNext(instance);
        }
    }

    private void executeNext(WorkflowInstance instance) {
        String node = instance.getCurrentNodeId();
        log.info("Executing node: {} inside instance: {}", node, instance.getId());

        // Simple mock node loop mapping definition
        if ("node_1".equalsIgnoreCase(node)) {
            // Check out book (Tool Node) -> Proceed to n2
            instance.setCurrentNodeId("node_2");
            instance.setLastUpdated(LocalDateTime.now());
            instanceRepository.save(instance);
            executeNext(instance);
        } else if ("node_2".equalsIgnoreCase(node)) {
            // Needs Librarian Review (Approval Node) -> Pause
            instance.setStatus("WAITING_APPROVAL");
            instance.setLastUpdated(LocalDateTime.now());
            instanceRepository.save(instance);
            log.info("Workflow paused. Waiting for human approval on node_2.");
        } else if ("node_3".equalsIgnoreCase(node)) {
            // Send Alert notification (Notification Node) -> Complete
            instance.setStatus("COMPLETED");
            instance.setCurrentNodeId(null);
            instance.setLastUpdated(LocalDateTime.now());
            instanceRepository.save(instance);
            log.info("Workflow execution completed successfully.");
        }
    }
}
