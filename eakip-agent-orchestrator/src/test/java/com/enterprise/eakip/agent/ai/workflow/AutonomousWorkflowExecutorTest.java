package com.enterprise.eakip.agent.ai.workflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutonomousWorkflowExecutorTest {

    @Mock
    private WorkflowDefinitionRepository definitionRepository;

    @Mock
    private WorkflowInstanceRepository instanceRepository;

    private AutonomousWorkflowExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new AutonomousWorkflowExecutor(instanceRepository, definitionRepository);
    }

    @Test
    void startExecution_RunsNodes_PausesAtApprovalNode() {
        // Arrange
        UUID defId = UUID.randomUUID();
        WorkflowDefinition definition = WorkflowDefinition.builder()
                .name("Borrow Checkout Flow")
                .triggerType("BORROW_CREATED")
                .build();
        definition.setId(defId);

        when(definitionRepository.findById(defId)).thenReturn(Optional.of(definition));
        
        WorkflowInstance mockInstance = WorkflowInstance.builder()
                .definition(definition)
                .currentNodeId("node_1")
                .status("RUNNING")
                .build();
        when(instanceRepository.save(any(WorkflowInstance.class))).thenReturn(mockInstance);

        // Act
        WorkflowInstance result = executor.startExecution(defId);

        // Assert
        assertNotNull(result);
        assertEquals("WAITING_APPROVAL", result.getStatus());
        assertEquals("node_2", result.getCurrentNodeId()); // paused at approval point
    }
}
