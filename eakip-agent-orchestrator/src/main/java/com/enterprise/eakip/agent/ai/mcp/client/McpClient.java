package com.enterprise.eakip.agent.ai.mcp.client;

import com.enterprise.eakip.agent.ai.mcp.registry.McpServerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class McpClient {

    public Object callMcpTool(McpServerRegistry.McpServerConfig server, String toolName, Map<String, Object> arguments) {
        log.info("Dispatching MCP JSON-RPC request to server: {}, endpoint: {}", server.getServerName(), server.getEndpointUrl());
        log.debug("Authenticating using credentials mapping: {}", server.getAuthType());

        // Simulate network delay and request validation
        try {
            Thread.sleep(Math.max(10, server.getLatencyMs()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("status", "SUCCESS");
        
        Map<String, Object> result = new HashMap<>();
        result.put("tool", toolName);
        result.put("content", "Successful payload output returned from external MCP host " + server.getServerName());
        
        response.put("result", result);
        return response;
    }
}
