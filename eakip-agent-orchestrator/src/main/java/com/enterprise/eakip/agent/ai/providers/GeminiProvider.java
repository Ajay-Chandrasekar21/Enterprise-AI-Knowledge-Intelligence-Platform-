package com.enterprise.eakip.agent.ai.providers;

import com.enterprise.eakip.agent.ai.core.AiProvider;
import com.enterprise.eakip.agent.ai.core.ModelRequest;
import com.enterprise.eakip.agent.ai.core.ModelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiProvider implements AiProvider {

    @Override
    public ModelResponse generate(ModelRequest request) {
        log.info("Executing chat generation via Google Gemini API");
        // Concrete simulation for foundation layers
        String responseContent = "Gemini Response context for user query: " + request.getUserPrompt();
        
        return ModelResponse.builder()
                .content(responseContent)
                .confidenceScore(0.95)
                .citations(new ArrayList<>())
                .usage(ModelResponse.TokenUsage.builder()
                        .inputTokens(120)
                        .outputTokens(250)
                        .latencyMs(1200L)
                        .build())
                .build();
    }

    @Override
    public Flux<String> stream(ModelRequest request) {
        log.info("Executing streaming chat generation via Google Gemini API");
        return Flux.just("Gemini ", "streaming ", "tokens ", "for ", request.getUserPrompt());
    }

    @Override
    public String getProviderName() {
        return "GEMINI";
    }
}
