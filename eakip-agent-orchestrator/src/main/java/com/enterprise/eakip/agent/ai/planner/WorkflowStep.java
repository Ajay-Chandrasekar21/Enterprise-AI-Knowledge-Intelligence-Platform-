package com.enterprise.eakip.agent.ai.planner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStep {
    private Integer stepIndex;
    private String agentName;
    private String description;
    @Builder.Default
    private String status = "PENDING"; // PENDING, EXECUTING, COMPLETED, FAILED
    private String output;
}
