package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.agent.ai.tools.Tool;
import com.enterprise.eakip.agent.ai.tools.ToolRegistry;
import com.enterprise.eakip.agent.ai.tools.core.ToolManifest;
import com.enterprise.eakip.agent.ai.tools.executor.ToolExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/tools")
@RequiredArgsConstructor
@Tag(name = "Agent Tools Ecosystem API", description = "Endpoints for dynamic tools discovery, manifests inspection, and retries wrapper executions")
public class ToolMarketplaceController {

    private final ToolRegistry toolRegistry;
    private final ToolExecutor toolExecutor;

    @GetMapping
    @Operation(summary = "List registered marketplace tools", description = "Queries all active tool components registered in the Spring container")
    public ResponseEntity<ApiResponse<List<ToolManifest>>> listTools() {
        List<ToolManifest> manifests = new ArrayList<>();
        for (Tool tool : toolRegistry.getAllTools()) {
            // Deduce Category from naming tags
            String category = "AI_UTILITY";
            String name = tool.getName().toLowerCase();
            if (name.contains("library") || name.contains("borrow") || name.contains("return") || name.contains("reservation") || name.contains("fine")) {
                category = "LIBRARY";
            } else if (name.contains("knowledge") || name.contains("citation") || name.contains("summary") || name.contains("rag")) {
                category = "KNOWLEDGE";
            } else if (name.contains("analytics") || name.contains("report") || name.contains("statistics")) {
                category = "ANALYTICS";
            } else if (name.contains("user") || name.contains("profile") || name.contains("history")) {
                category = "USER";
            } else if (name.contains("communication") || name.contains("email") || name.contains("webhook") || name.contains("notification")) {
                category = "COMMUNICATION";
            }

            manifests.add(ToolManifest.builder()
                    .name(tool.getName())
                    .description(tool.getDescription())
                    .category(category)
                    .parameters(tool.getParameters())
                    .requiredPermission("USER")
                    .version("1.0.0")
                    .build());
        }
        return ResponseEntity.ok(ApiResponse.success("Tools registry loaded", manifests));
    }

    @PostMapping("/execute")
    @Operation(summary = "Execute tool operation", description = "Executes the target tool with parameters routing through retry policies")
    public ResponseEntity<ApiResponse<Object>> executeTool(
            @RequestParam String toolName,
            @RequestBody Map<String, Object> arguments) {
            
        Object output = toolRegistry.getTool(toolName)
                .map(tool -> toolExecutor.executeWithPolicies(tool, arguments))
                .orElseThrow(() -> new IllegalArgumentException("No tool found matching name: " + toolName));

        return ResponseEntity.ok(ApiResponse.success("Tool execution completed successfully", output));
    }

    @GetMapping("/history")
    @Operation(summary = "Query tool execution log logs", description = "Returns past execution records, latency times, and retry status details")
    public ResponseEntity<ApiResponse<Map<String, List<ToolExecutor.ExecutionRecord>>>> getHistory() {
        return ResponseEntity.ok(ApiResponse.success("Execution history loaded", toolExecutor.getAllHistory()));
    }
}
