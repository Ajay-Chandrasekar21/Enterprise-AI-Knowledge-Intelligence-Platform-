package com.enterprise.eakip.rag.embedding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockEmbeddingService implements EmbeddingService {

    private final StringRedisTemplate redisTemplate;
    private static final String EMBEDDING_CACHE_PREFIX = "eakip:embedding:";

    @Override
    public double[] generateEmbedding(String text) {
        String cacheKey = EMBEDDING_CACHE_PREFIX + Math.abs(text.hashCode());
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("Embedding cache hit in Redis");
                return Arrays.stream(cached.split(",")).mapToDouble(Double::parseDouble).toArray();
            }
        } catch (Exception e) {
            log.warn("Redis operations failed. Computing embedding directly.");
        }

        log.debug("Generating mock embedding vector for text chunk");
        double[] vector = new double[128]; // Mock 128-dimensional embedding
        int code = text.hashCode();
        for (int i = 0; i < 128; i++) {
            vector[i] = Math.sin(code + i) * 0.5 + 0.5;
        }

        try {
            String vectorStr = Arrays.toString(vector).replaceAll("[\\[\\]\\s]", "");
            redisTemplate.opsForValue().set(cacheKey, vectorStr, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("Redis save failed.");
        }

        return vector;
    }
}
