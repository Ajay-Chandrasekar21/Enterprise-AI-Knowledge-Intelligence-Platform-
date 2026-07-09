package com.enterprise.eakip.rag.vector;

import com.enterprise.eakip.rag.embedding.EmbeddingService;
import com.enterprise.eakip.rag.model.ChunkNode;
import com.enterprise.eakip.rag.repository.ChunkNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final ChunkNodeRepository chunkRepository;
    private final EmbeddingService embeddingService;

    public void store(ChunkNode chunk, double[] vector) {
        String vectorStr = Arrays.toString(vector).replaceAll("[\\[\\]\\s]", "");
        chunk.setEmbeddingVector(vectorStr);
        chunkRepository.save(chunk);
    }

    public List<SearchResult> search(String queryText, int topK) {
        log.info("Executing semantic top-K search in vector registry. TopK={}", topK);
        double[] queryVector = embeddingService.generateEmbedding(queryText);
        
        List<ChunkNode> allChunks = chunkRepository.findAll();
        List<SearchResult> results = new ArrayList<>();

        for (ChunkNode chunk : allChunks) {
            if (chunk.getEmbeddingVector() == null) continue;
            try {
                double[] chunkVector = Arrays.stream(chunk.getEmbeddingVector().split(","))
                        .mapToDouble(Double::parseDouble)
                        .toArray();

                double score = cosineSimilarity(queryVector, chunkVector);
                results.add(new SearchResult(chunk, score));
            } catch (Exception e) {
                log.warn("Failed to parse vector for chunk: {}", chunk.getId());
            }
        }

        return results.stream()
                .sorted(Comparator.comparingDouble(SearchResult::getScore).reversed())
                .limit(topK)
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < Math.min(vectorA.length, vectorB.length); i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return (normA == 0.0 || normB == 0.0) ? 0.0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @lombok.Getter
    @RequiredArgsConstructor
    public static class SearchResult {
        private final ChunkNode chunkNode;
        private final double score;
    }
}
