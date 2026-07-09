package com.enterprise.eakip.document.chunking;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SmartChunkGenerator {

    public List<String> generateChunks(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return chunks;
        }

        int textLength = text.length();
        int start = 0;

        while (start < textLength) {
            int end = Math.min(start + chunkSize, textLength);
            String chunk = text.substring(start, end);
            chunks.add(chunk);

            if (end == textLength) {
                break;
            }
            start += (chunkSize - overlap);
        }

        return chunks;
    }
}
