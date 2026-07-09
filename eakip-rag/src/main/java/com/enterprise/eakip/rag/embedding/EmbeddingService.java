package com.enterprise.eakip.rag.embedding;

public interface EmbeddingService {
    double[] generateEmbedding(String text);
}
