package com.enterprise.eakip.agent.ai.runtime;

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
public class AgentResult {
    private String agentName;
    private String outputContent;
    private AgentState state;
    private Double confidence;
    private String errorMessage;
    private Long executionTimeMs;
}
