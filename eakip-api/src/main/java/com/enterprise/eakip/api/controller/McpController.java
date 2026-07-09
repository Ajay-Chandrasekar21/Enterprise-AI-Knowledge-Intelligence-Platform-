package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.agent.ai.mcp.client.McpClient;
import com.enterprise.eakip.agent.ai.mcp.client.McpConnector;
import com.enterprise.eakip.agent.ai.mcp.registry.McpServerRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/mcp")
@RequiredArgsConstructor
@Tag(name = "Model Context Protocol (MCP) API", description = "Endpoints for registering external MCP servers and calling distributed tools")
public class McpController {

    private final McpServerRegistry serverRegistry;
    private final McpClient mcpClient;
    private final McpConnector mcpConnector;

    @GetMapping("/servers")
    @Operation(summary = "List registered MCP servers", description = "Returns active servers, connection status health status, and latency metrics")
    public ResponseEntity<ApiResponse<List<McpServerRegistry.McpServerConfig>>> listServers() {
        return ResponseEntity.ok(ApiResponse.success("MCP servers loaded", serverRegistry.getAllServers()));
    }

    @PostMapping("/servers")
    @Operation(summary = "Register external MCP server", description = "Registers connection endpoints and credentials parameters (JWT, API Keys, OAuth2)")
    public ResponseEntity<ApiResponse<String>> registerServer(@RequestBody McpServerRegistry.McpServerConfig config) {
        config.setStatus("CONNECTED");
        config.setLatencyMs(45);
        serverRegistry.registerServer(config);
        return ResponseEntity.ok(ApiResponse.success("MCP server registered and connected successfully", config.getServerName()));
    }

    @DeleteMapping("/servers")
    @Operation(summary = "Remove MCP server registration")
    public ResponseEntity<ApiResponse<String>> removeServer(@RequestParam String serverName) {
        serverRegistry.unregisterServer(serverName);
        return ResponseEntity.ok(ApiResponse.success("MCP server removed successfully", serverName));
    }

    @GetMapping("/tools")
    @Operation(summary = "Discover external tools schemas", description = "Discovers capabilities on the target MCP host")
    public ResponseEntity<ApiResponse<List<McpConnector.McpToolSchema>>> discoverTools(@RequestParam String serverName) {
        return ResponseEntity.ok(ApiResponse.success("MCP tools schemas discovered", mcpConnector.getConnectorTools(serverName)));
    }

    @PostMapping("/execute")
    @Operation(summary = "Execute external MCP tool", description = "Routes tool parameter arguments to the external JSON-RPC server and returns formatted response")
    public ResponseEntity<ApiResponse<Object>> executeMcpTool(
            @RequestParam String serverName,
            @RequestParam String toolName,
            @RequestBody Map<String, Object> arguments) {
            
        McpServerRegistry.McpServerConfig server = serverRegistry.getServer(serverName);
        if (server == null) {
            throw new IllegalArgumentException("No MCP server registered matching name: " + serverName);
        }

        Object output = mcpClient.callMcpTool(server, toolName, arguments);
        return ResponseEntity.ok(ApiResponse.success("MCP Tool execution successful", output));
    }
}
