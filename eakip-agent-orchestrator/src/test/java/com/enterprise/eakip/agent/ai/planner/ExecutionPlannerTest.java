package com.enterprise.eakip.agent.ai.planner;

import com.enterprise.eakip.agent.ai.orchestrator.IntentEngine;
import com.enterprise.eakip.agent.ai.orchestrator.IntentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionPlannerTest {

    @Mock
    private IntentEngine intentEngine;

    @InjectMocks
    private ExecutionPlanner executionPlanner;

    @BeforeEach
    void setUp() {
    }

    @Test
    void plan_BookSearchIntent_CreatesSequentialWorkflow() {
        // Arrange
        String query = "Find books on clean code";
        when(intentEngine.detectIntent(query)).thenReturn(new IntentEngine.IntentResult(IntentType.BOOK_SEARCH, 0.95));

        // Act
        ExecutionPlan plan = executionPlanner.plan(query);

        // Assert
        assertNotNull(plan);
        assertEquals(WorkflowType.SEQUENTIAL, plan.getType());
        assertEquals(2, plan.getSteps().size());
        assertEquals("BOOK_DISCOVERY_AGENT", plan.getSteps().get(0).getAgentName());
        assertEquals("SEMANTIC_SEARCH_AGENT", plan.getSteps().get(1).getAgentName());
    }

    @Test
    void plan_AnalyticsIntent_CreatesSingleAgentWorkflow() {
        // Arrange
        String query = "Show active library statistics";
        when(intentEngine.detectIntent(query)).thenReturn(new IntentEngine.IntentResult(IntentType.ANALYTICS, 0.90));

        // Act
        ExecutionPlan plan = executionPlanner.plan(query);

        // Assert
        assertNotNull(plan);
        assertEquals(WorkflowType.SINGLE_AGENT, plan.getType());
        assertEquals(1, plan.getSteps().size());
        assertEquals("ANALYTICS_AGENT", plan.getSteps().get(0).getAgentName());
    }
}
