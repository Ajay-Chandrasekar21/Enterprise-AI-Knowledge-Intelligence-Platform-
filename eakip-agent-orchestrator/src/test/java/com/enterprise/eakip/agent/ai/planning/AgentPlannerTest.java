package com.enterprise.eakip.agent.ai.planning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentPlannerTest {

    private AgentPlanner planner;

    @BeforeEach
    void setUp() {
        planner = new AgentPlanner();
    }

    @Test
    void generatePlan_ForInterviewQuery_BuildsComplexGraph() {
        // Act
        TaskGraph graph = planner.generatePlan("I want beginner Java books to prepare for interviews.");

        // Assert
        assertNotNull(graph);
        assertEquals(4, graph.getNodes().size());
        assertEquals("BOOK_DISCOVERY_AGENT", graph.getNodes().get(0).getAssignedAgent());
        assertEquals("RECOMMENDATION_AGENT", graph.getNodes().get(1).getAssignedAgent());
        assertTrue(graph.getEdges().size() >= 3);
    }

    @Test
    void generatePlan_ForDefaultQuery_BuildsSimpleGraph() {
        // Act
        TaskGraph graph = planner.generatePlan("Clean Architecture");

        // Assert
        assertNotNull(graph);
        assertEquals(2, graph.getNodes().size());
        assertEquals("BOOK_DISCOVERY_AGENT", graph.getNodes().get(0).getAssignedAgent());
        assertEquals("SEMANTIC_SEARCH_AGENT", graph.getNodes().get(1).getAssignedAgent());
    }
}
