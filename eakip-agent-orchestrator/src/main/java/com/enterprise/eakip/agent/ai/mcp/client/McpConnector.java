package com.enterprise.eakip.agent.ai.mcp.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class McpConnector {

    public List<McpToolSchema> getConnectorTools(String serverName) {
        List<McpToolSchema> schemas = new ArrayList<>();
        
        if ("github".equalsIgnoreCase(serverName)) {
            schemas.add(new McpToolSchema("createIssue", "Creates a GitHub issue in the target repository", Map.of("repo", "String", "title", "String")));
            schemas.add(new McpToolSchema("listPRs", "Lists open pull requests", Map.of("repo", "String")));
        } else if ("jira".equalsIgnoreCase(serverName)) {
            schemas.add(new McpToolSchema("createTicket", "Creates a Jira ticket", Map.of("project", "String", "summary", "String")));
        } else if ("slack".equalsIgnoreCase(serverName)) {
            schemas.add(new McpToolSchema("postMessage", "Dispatches alert messages to Slack channels", Map.of("channel", "String", "text", "String")));
        } else {
            // General connector layout stubs
            schemas.add(new McpToolSchema("queryData", "Executes data query operations", Map.of("query", "String")));
        }

        return schemas;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class McpToolSchema {
        private String name;
        private String description;
        private Map<String, String> parameters;
    }
}
