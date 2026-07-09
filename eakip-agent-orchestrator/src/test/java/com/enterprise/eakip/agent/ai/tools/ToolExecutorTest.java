package com.enterprise.eakip.agent.ai.tools;

import com.enterprise.eakip.agent.ai.tools.executor.ToolExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToolExecutorTest {

    private ToolExecutor toolExecutor;

    @Mock
    private Tool mockTool;

    @BeforeEach
    void setUp() {
        toolExecutor = new ToolExecutor();
    }

    @Test
    void executeWithPolicies_Success_SavesHistory() {
        // Arrange
        Map<String, Object> args = new HashMap<>();
        args.put("key", "val");

        when(mockTool.getName()).thenReturn("MockTool");
        when(mockTool.execute(args)).thenReturn("Success Output");

        // Act
        Object result = toolExecutor.executeWithPolicies(mockTool, args);

        // Assert
        assertEquals("Success Output", result);
        List<ToolExecutor.ExecutionRecord> records = toolExecutor.getHistory("MockTool");
        assertEquals(1, records.size());
        assertEquals("SUCCESS", records.get(0).getStatus());
    }

    @Test
    void executeWithPolicies_FailureAfterRetries_ThrowsException() {
        // Arrange
        Map<String, Object> args = new HashMap<>();
        when(mockTool.getName()).thenReturn("MockTool");
        when(mockTool.execute(args)).thenThrow(new RuntimeException("Network Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> toolExecutor.executeWithPolicies(mockTool, args));
        verify(mockTool, times(3)).execute(args);
    }
}
