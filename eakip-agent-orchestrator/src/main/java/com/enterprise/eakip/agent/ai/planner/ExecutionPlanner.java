package com.enterprise.eakip.agent.ai.planner;

import com.enterprise.eakip.agent.ai.orchestrator.IntentEngine;
import com.enterprise.eakip.agent.ai.orchestrator.IntentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExecutionPlanner {

    private final IntentEngine intentEngine;

    public ExecutionPlan plan(String query) {
        IntentEngine.IntentResult result = intentEngine.detectIntent(query);
        List<WorkflowStep> steps = new ArrayList<>();
        
        String planId = UUID.randomUUID().toString();
        WorkflowType type = WorkflowType.SINGLE_AGENT;

        if (query.toLowerCase().contains("interview") || query.toLowerCase().contains("prepare")) {
            steps.add(WorkflowStep.builder().stepIndex(1).agentName("BOOK_DISCOVERY_AGENT").description("Find beginner Java books in catalog index").build());
            steps.add(WorkflowStep.builder().stepIndex(2).agentName("RECOMMENDATION_AGENT").description("Generate personalized readings matching user career focus").build());
            steps.add(WorkflowStep.builder().stepIndex(3).agentName("ANALYTICS_AGENT").description("Examine borrow counts and popularity for Java references").build());
            steps.add(WorkflowStep.builder().stepIndex(4).agentName("NOTIFICATION_AGENT").description("Trigger borrow reminders and notifications setup").build());
            type = WorkflowType.SEQUENTIAL;
        } else if (result.getIntent() == IntentType.BOOK_SEARCH) {
            steps.add(WorkflowStep.builder().stepIndex(1).agentName("BOOK_DISCOVERY_AGENT").description("Find matching books in the catalog index").build());
            steps.add(WorkflowStep.builder().stepIndex(2).agentName("SEMANTIC_SEARCH_AGENT").description("Rank results based on contextual vector relevance").build());
            type = WorkflowType.SEQUENTIAL;
        } else if (result.getIntent() == IntentType.ANALYTICS) {
            steps.add(WorkflowStep.builder().stepIndex(1).agentName("ANALYTICS_AGENT").description("Compute borrow counts and generate monthly metrics charts").build());
            type = WorkflowType.SINGLE_AGENT;
        } else if (result.getIntent() == IntentType.LIBRARY_OPERATIONS) {
            steps.add(WorkflowStep.builder().stepIndex(1).agentName("NOTIFICATION_AGENT").description("Coordinate active borrow periods or check holds queues").build());
            type = WorkflowType.SINGLE_AGENT;
        } else {
            steps.add(WorkflowStep.builder().stepIndex(1).agentName("GENERAL_AGENT").description("Resolve general knowledge requests").build());
            type = WorkflowType.SINGLE_AGENT;
        }

        return ExecutionPlan.builder()
                .planId(planId)
                .steps(steps)
                .type(type)
                .build();
    }
}
