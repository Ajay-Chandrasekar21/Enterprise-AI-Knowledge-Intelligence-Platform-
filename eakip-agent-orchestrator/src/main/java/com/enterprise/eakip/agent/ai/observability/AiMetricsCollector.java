package com.enterprise.eakip.agent.ai.observability;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class AiMetricsCollector {

    private final AtomicLong totalTokensConsumed = new AtomicLong(0);
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicLong totalLatencyMs = new AtomicLong(0);

    public void logMetrics(String provider, int inputTokens, int outputTokens, long latencyMs) {
        totalTokensConsumed.addAndGet(inputTokens + outputTokens);
        totalRequests.incrementAndGet();
        totalLatencyMs.addAndGet(latencyMs);
        
        log.info("LLM Metrics Logged - Provider: {}, Input Tokens: {}, Output Tokens: {}, Latency: {} ms",
                provider, inputTokens, outputTokens, latencyMs);
    }

    public void logFailure(String provider, String error) {
        failedRequests.incrementAndGet();
        totalRequests.incrementAndGet();
        log.error("LLM Provider Failure Logged - Provider: {}, Error: {}", provider, error);
    }

    public long getTotalTokens() { return totalTokensConsumed.get(); }
    public long getRequestsCount() { return totalRequests.get(); }
    public long getFailuresCount() { return failedRequests.get(); }
    
    public double getAverageLatencyMs() {
        long reqs = totalRequests.get();
        return reqs == 0 ? 0.0 : (double) totalLatencyMs.get() / reqs;
    }
}
