package com.enterprise.eakip.agent.ai.tools.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolManifest {
    private String name;
    private String description;
    private String category; // LIBRARY, KNOWLEDGE, ANALYTICS, USER, COMMUNICATION, AI_UTILITY
    private Map<String, String> parameters;
    private String requiredPermission; // ADMIN, LIBRARIAN, USER
    private String version;
}
