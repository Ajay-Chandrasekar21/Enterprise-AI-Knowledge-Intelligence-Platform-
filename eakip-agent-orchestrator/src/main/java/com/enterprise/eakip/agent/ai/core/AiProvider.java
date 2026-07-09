package com.enterprise.eakip.agent.ai.core;

import reactor.core.publisher.Flux;

public interface AiProvider {
    ModelResponse generate(ModelRequest request);
    Flux<String> stream(ModelRequest request);
    String getProviderName();
}
