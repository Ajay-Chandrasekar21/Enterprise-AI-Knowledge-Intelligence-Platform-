package com.enterprise.eakip.agent.ai.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelResponse {
    private String content;
    @Builder.Default
    private Double confidenceScore = 1.0;
    @Builder.Default
    private List<Citation> citations = new ArrayList<>();
    private TokenUsage usage;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Citation {
        private String bookId;
        private String bookTitle;
        private Integer pageNumber;
        private String snippet;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenUsage {
        private Integer inputTokens;
        private Integer outputTokens;
        private Long latencyMs;
    }
}
