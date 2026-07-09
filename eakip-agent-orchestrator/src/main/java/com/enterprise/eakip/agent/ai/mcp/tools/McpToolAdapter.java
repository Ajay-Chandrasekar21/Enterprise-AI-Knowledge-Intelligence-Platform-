package com.enterprise.eakip.agent.ai.mcp.tools;

import com.enterprise.eakip.agent.ai.mcp.client.McpClient;
import com.enterprise.eakip.agent.ai.mcp.registry.McpServerRegistry;
import com.enterprise.eakip.agent.ai.tools.Tool;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class McpToolAdapter implements Tool {

    private final String name;
    private final String description;
    private final Map<String, String> parameters;
    private final McpServerRegistry.McpServerConfig serverConfig;
    private final McpClient mcpClient;

    @Override
    public String getName() {
        return "MCP_" + serverConfig.getServerName().toUpperCase() + "_" + name;
    }

    @Override
    public String getDescription() {
        return "[MCP Proxy Tool] " + description;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        return mcpClient.callMcpTool(serverConfig, name, arguments);
    }
}
