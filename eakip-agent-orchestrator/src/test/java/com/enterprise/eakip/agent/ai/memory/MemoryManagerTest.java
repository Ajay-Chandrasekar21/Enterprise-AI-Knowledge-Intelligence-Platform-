package com.enterprise.eakip.agent.ai.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemoryManagerTest {

    @Mock
    private MemoryNodeRepository memoryRepository;

    private MemoryManager memoryManager;

    @BeforeEach
    void setUp() {
        memoryManager = new MemoryManager(memoryRepository);
    }

    @Test
    void saveMemory_PersistsNodeCorrectly() {
        // Arrange
        UUID userId = UUID.randomUUID();
        MemoryNode node = MemoryNode.builder()
                .userId(userId)
                .memoryType("EPISODIC")
                .content("User read Clean Code book")
                .relevanceScore(0.95)
                .build();

        when(memoryRepository.save(any(MemoryNode.class))).thenReturn(node);

        // Act
        MemoryNode saved = memoryManager.saveMemory(userId, "EPISODIC", "User read Clean Code book", 0.95);

        // Assert
        assertNotNull(saved);
        assertEquals("EPISODIC", saved.getMemoryType());
        assertEquals("User read Clean Code book", saved.getContent());
    }

    @Test
    void retrieveMemories_ReturnsRankedNodes() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<MemoryNode> list = new ArrayList<>();
        list.add(MemoryNode.builder().userId(userId).memoryType("EPISODIC").content("Low relevance").relevanceScore(0.2).build());
        list.add(MemoryNode.builder().userId(userId).memoryType("EPISODIC").content("High relevance").relevanceScore(0.99).build());

        when(memoryRepository.searchMemories(userId, "relevance")).thenReturn(list);

        // Act
        List<MemoryNode> results = memoryManager.retrieveMemories(userId, "relevance", 5);

        // Assert
        assertEquals(2, results.size());
        assertEquals("High relevance", results.get(0).getContent()); // ranked first
    }
}
