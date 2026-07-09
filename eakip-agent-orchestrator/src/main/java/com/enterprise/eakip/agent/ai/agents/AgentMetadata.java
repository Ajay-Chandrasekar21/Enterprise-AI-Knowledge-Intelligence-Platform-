package com.enterprise.eakip.agent.ai.agents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentMetadata {
    private String name;
    private String role;
    private String description;
    private List<String> allowedTools;
    @Builder.Default
    private Double confidenceThreshold = 0.70;
}
