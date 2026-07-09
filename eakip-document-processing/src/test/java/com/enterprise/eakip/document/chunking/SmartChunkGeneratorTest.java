package com.enterprise.eakip.document.chunking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmartChunkGeneratorTest {

    private SmartChunkGenerator chunkGenerator;

    @BeforeEach
    void setUp() {
        chunkGenerator = new SmartChunkGenerator();
    }

    @Test
    void generateChunks_SplitsText_WithSlidingWindow() {
        // Arrange
        String sampleText = "This is a sample document content that we want to parse and split into sliding chunks.";
        int chunkSize = 20;
        int overlap = 5;

        // Act
        List<String> chunks = chunkGenerator.generateChunks(sampleText, chunkSize, overlap);

        // Assert
        assertNotNull(chunks);
        assertTrue(chunks.size() > 1);
        for (String chunk : chunks) {
            assertTrue(chunk.length() <= chunkSize);
        }
        // Verify overlapping content mapping
        assertEquals("This is a sample doc", chunks.get(0));
    }
}
