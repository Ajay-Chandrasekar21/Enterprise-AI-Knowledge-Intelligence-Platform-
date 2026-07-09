package com.enterprise.eakip.agent.ai.planner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionPlan {
    private String planId;
    @Builder.Default
    private List<WorkflowStep> steps = new ArrayList<>();
    private WorkflowType type;
    @Builder.Default
    private boolean completed = false;
}
