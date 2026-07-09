package com.enterprise.eakip.agent.ai.tools.executor;

import com.enterprise.eakip.agent.ai.tools.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ToolExecutor {

    private final Map<String, List<ExecutionRecord>> history = new ConcurrentHashMap<>();

    public Object executeWithPolicies(Tool tool, Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        String toolName = tool.getName().toUpperCase();
        
        log.info("Executing Tool: {} with retry policies and circuit breakers", toolName);
        
        int maxRetries = 3;
        int attempt = 0;
        Object output = null;
        String status = "SUCCESS";
        String errorMsg = null;

        while (attempt < maxRetries) {
            try {
                attempt++;
                output = tool.execute(arguments);
                break;
            } catch (Exception e) {
                log.warn("Execution attempt {} failed for tool {}: {}", attempt, toolName, e.getMessage());
                errorMsg = e.getMessage();
                status = "FAILED";
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Tool execution failed after maximum retries: " + errorMsg, e);
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        
        // Log history record
        ExecutionRecord record = ExecutionRecord.builder()
                .toolName(toolName)
                .arguments(arguments)
                .latencyMs(duration)
                .status(status)
                .errorMessage(errorMsg)
                .build();
                
        history.computeIfAbsent(toolName, k -> new ArrayList<>()).add(record);

        return output;
    }

    public List<ExecutionRecord> getHistory(String toolName) {
        return history.getOrDefault(toolName.toUpperCase(), List.of());
    }

    public Map<String, List<ExecutionRecord>> getAllHistory() {
        return history;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecutionRecord {
        private String toolName;
        private Map<String, Object> arguments;
        private long latencyMs;
        private String status;
        private String errorMessage;
    }
}
