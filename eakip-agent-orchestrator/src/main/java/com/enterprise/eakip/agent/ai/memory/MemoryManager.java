package com.enterprise.eakip.agent.ai.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryManager {

    private final MemoryNodeRepository memoryRepository;

    @Transactional
    public MemoryNode saveMemory(UUID userId, String type, String content, Double score) {
        log.info("Saving memory node. Type={}, Score={}", type, score);
        MemoryNode node = MemoryNode.builder()
                .userId(userId)
                .memoryType(type.toUpperCase())
                .content(content)
                .relevanceScore(score)
                .build();
        return memoryRepository.save(node);
    }

    public List<MemoryNode> retrieveMemories(UUID userId, String query, int limit) {
        log.info("Querying memories. Query={}, Limit={}", query, limit);
        List<MemoryNode> list = memoryRepository.searchMemories(userId, query);
        
        // Rank by relevance score descending and update last accessed date
        return list.stream()
                .sorted(Comparator.comparingDouble(MemoryNode::getRelevanceScore).reversed())
                .limit(limit)
                .peek(node -> {
                    node.setLastAccessedDate(LocalDateTime.now());
                    memoryRepository.save(node);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void compressMemories(UUID userId) {
        log.info("Running memory compression/summarization algorithms over user: {}", userId);
        List<MemoryNode> list = memoryRepository.findByUserId(userId);
        
        // Expiration cleanup
        LocalDateTime cutoff = LocalDateTime.now();
        List<MemoryNode> expired = list.stream()
                .filter(node -> node.getCreatedDate().plusDays(node.getRetentionDays()).isBefore(cutoff))
                .collect(Collectors.toList());

        if (!expired.isEmpty()) {
            log.info("Expiring {} outdated memory nodes", expired.size());
            memoryRepository.deleteAll(expired);
        }
    }
}
