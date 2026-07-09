package com.enterprise.eakip.agent.ai.orchestrator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class IntentEngine {

    public IntentResult detectIntent(String query) {
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("book") || lowerQuery.contains("isbn") || lowerQuery.contains("author") || lowerQuery.contains("catalog")) {
            return new IntentResult(IntentType.BOOK_SEARCH, 0.95);
        }
        if (lowerQuery.contains("metric") || lowerQuery.contains("stat") || lowerQuery.contains("analytics") || lowerQuery.contains("report")) {
            return new IntentResult(IntentType.ANALYTICS, 0.90);
        }
        if (lowerQuery.contains("borrow") || lowerQuery.contains("return") || lowerQuery.contains("reserve") || lowerQuery.contains("checkout")) {
            return new IntentResult(IntentType.LIBRARY_OPERATIONS, 0.95);
        }
        return new IntentResult(IntentType.GENERAL_KNOWLEDGE, 0.70);
    }

    @Getter
    @AllArgsConstructor
    public static class IntentResult {
        private final IntentType intent;
        private final double confidenceScore;
    }
}
