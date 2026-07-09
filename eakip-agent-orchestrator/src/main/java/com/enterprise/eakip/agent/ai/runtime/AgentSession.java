package com.enterprise.eakip.agent.ai.runtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSession {
    private String sessionId;
    private UUID userId;
    private String userQuery;
    @Builder.Default
    private Map<String, Object> sessionVariables = new HashMap<>();
}
