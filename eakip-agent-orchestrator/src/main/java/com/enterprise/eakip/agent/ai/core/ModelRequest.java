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
public class ModelRequest {
    private String systemPrompt;
    private String userPrompt;
    @Builder.Default
    private List<ChatMessage> chatHistory = new ArrayList<>();
    @Builder.Default
    private Double temperature = 0.7;
    @Builder.Default
    private Boolean stream = false;
    private String jsonSchema;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessage {
        private String role; // "user" or "assistant"
        private String content;
    }
}
