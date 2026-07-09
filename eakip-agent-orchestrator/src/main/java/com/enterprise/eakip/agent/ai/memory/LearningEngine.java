package com.enterprise.eakip.agent.ai.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningEngine {

    private final MemoryManager memoryManager;

    @Transactional
    public void recordFeedback(UUID userId, String targetEntity, int rating, String feedbackMsg) {
        log.info("Recording feedback score. Target: {}, Rating: {}/5", targetEntity, rating);
        
        double relevanceAdjustment = (rating - 3) * 0.1; // positive/negative adjustment
        
        // Save preference learning memory
        String memoryContent = String.format("User rated '%s' value as %d. Feedback comment: '%s'",
                targetEntity, rating, feedbackMsg != null ? feedbackMsg : "none");

        memoryManager.saveMemory(
                userId,
                "PREFERENCE",
                memoryContent,
                0.8 + relevanceAdjustment
        );
        
        log.info("Preference memory updated based on dynamic learning adjustments");
    }
}
