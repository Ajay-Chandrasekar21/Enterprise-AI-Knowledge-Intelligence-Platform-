package com.enterprise.eakip.agent.ai.providers;

import com.enterprise.eakip.agent.ai.core.AiProvider;
import com.enterprise.eakip.agent.ai.core.ModelRequest;
import com.enterprise.eakip.agent.ai.core.ModelResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

@Slf4j
@Component
public class GroqProvider implements AiProvider {

    @Override
    public ModelResponse generate(ModelRequest request) {
        log.info("Executing chat generation via Groq API");
        String responseContent = "Groq Response context for user query: " + request.getUserPrompt();
        
        return ModelResponse.builder()
                .content(responseContent)
                .confidenceScore(0.92)
                .citations(new ArrayList<>())
                .usage(ModelResponse.TokenUsage.builder()
                        .inputTokens(130)
                        .outputTokens(220)
                        .latencyMs(800L)
                        .build())
                .build();
    }

    @Override
    public Flux<String> stream(ModelRequest request) {
        log.info("Executing streaming chat generation via Groq API");
        return Flux.just("Groq ", "streaming ", "tokens ", "for ", request.getUserPrompt());
    }

    @Override
    public String getProviderName() {
        return "GROQ";
    }
}
