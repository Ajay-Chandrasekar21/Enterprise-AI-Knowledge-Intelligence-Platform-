package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.agent.ai.memory.MemoryManager;
import com.enterprise.eakip.agent.ai.memory.MemoryNode;
import com.enterprise.eakip.agent.ai.memory.MemoryNodeRepository;
import com.enterprise.eakip.agent.ai.memory.LearningEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/memory")
@RequiredArgsConstructor
@Tag(name = "Agent Memory & Learning API", description = "Endpoints for episodic timelines, semantic searches, and dynamic learning feedbacks")
public class MemoryController {

    private final MemoryManager memoryManager;
    private final MemoryNodeRepository memoryRepository;
    private final LearningEngine learningEngine;

    // Fixed mockup UUID for simple local checks
    private static final UUID USER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @GetMapping("/timeline")
    @Operation(summary = "Query memory nodes timeline", description = "Returns active episodic, preference, and procedural memories sorted chronologically")
    public ResponseEntity<ApiResponse<List<MemoryNode>>> getTimeline() {
        // Automatically compress/expire memories before loading timeline
        memoryManager.compressMemories(USER_UUID);
        
        List<MemoryNode> memories = memoryRepository.findByUserId(USER_UUID);
        return ResponseEntity.ok(ApiResponse.success("Timeline loaded successfully", memories));
    }

    @GetMapping("/search")
    @Operation(summary = "Semantic search memory index", description = "Queries user memory registry using ranked relevance sorting")
    public ResponseEntity<ApiResponse<List<MemoryNode>>> searchMemories(@RequestParam String query) {
        List<MemoryNode> result = memoryManager.retrieveMemories(USER_UUID, query, 5);
        return ResponseEntity.ok(ApiResponse.success("Semantic memory search complete", result));
    }

    @PostMapping("/feedback")
    @Operation(summary = "Submit execution feedback", description = "Feeds user scores and ratings into the learning engine to optimize future outputs")
    public ResponseEntity<ApiResponse<String>> submitFeedback(
            @RequestParam String target,
            @RequestParam int rating,
            @RequestBody(required = false) String comment) {
            
        learningEngine.recordFeedback(USER_UUID, target, rating, comment);
        return ResponseEntity.ok(ApiResponse.success("Feedback submitted and preference memory updated", target));
    }

    @PostMapping("/preferences")
    @Operation(summary = "Explicitly update learning preferences")
    public ResponseEntity<ApiResponse<MemoryNode>> updatePreferences(@RequestBody Map<String, String> prefMap) {
        String content = "User set favorite categories interest tags: " + prefMap.get("interests");
        MemoryNode node = memoryManager.saveMemory(USER_UUID, "PREFERENCE", content, 1.0);
        return ResponseEntity.ok(ApiResponse.success("Explicit preference registered", node));
    }
}
