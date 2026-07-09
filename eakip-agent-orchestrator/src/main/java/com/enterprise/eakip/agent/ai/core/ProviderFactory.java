package com.enterprise.eakip.agent.ai.core;

import com.enterprise.eakip.agent.ai.providers.GeminiProvider;
import com.enterprise.eakip.agent.ai.providers.GroqProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderFactory {

    private final GeminiProvider geminiProvider;
    private final GroqProvider groqProvider;
    private final ApplicationEventPublisher eventPublisher;

    public ModelResponse execute(ModelRequest request) {
        try {
            log.info("Attempting primary LLM execution via Gemini Provider");
            return geminiProvider.generate(request);
        } catch (Exception e) {
            log.warn("Gemini Provider execution failed. Triggering automatic provider switching.", e);
            eventPublisher.publishEvent(new ProviderFailureEvent(this, "GEMINI", e.getMessage()));
            
            // Switch to backup provider
            log.info("Switching to backup LLM execution via Groq Provider");
            return groqProvider.generate(request);
        }
    }

    public Flux<String> executeStream(ModelRequest request) {
        try {
            return geminiProvider.stream(request);
        } catch (Exception e) {
            log.warn("Gemini streaming failed. Switching to Groq stream.", e);
            eventPublisher.publishEvent(new ProviderFailureEvent(this, "GEMINI_STREAM", e.getMessage()));
            return groqProvider.stream(request);
        }
    }

    public static class ProviderFailureEvent extends org.springframework.context.ApplicationEvent {
        private final String providerName;
        private final String errorMessage;

        public ProviderFailureEvent(Object source, String providerName, String errorMessage) {
            super(source);
            this.providerName = providerName;
            this.errorMessage = errorMessage;
        }

        public String getProviderName() { return providerName; }
        public String getErrorMessage() { return errorMessage; }
    }
}
