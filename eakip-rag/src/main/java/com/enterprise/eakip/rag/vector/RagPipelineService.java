package com.enterprise.eakip.rag.vector;

import com.enterprise.eakip.document.chunking.SmartChunkGenerator;
import com.enterprise.eakip.document.parser.DocumentParser;
import com.enterprise.eakip.rag.embedding.EmbeddingService;
import com.enterprise.eakip.rag.model.ChunkNode;
import com.enterprise.eakip.rag.model.DocumentNode;
import com.enterprise.eakip.rag.repository.DocumentNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagPipelineService {

    private final DocumentNodeRepository documentRepository;
    private final DocumentParser documentParser;
    private final SmartChunkGenerator chunkGenerator;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStore;
    private final KnowledgeGraphService knowledgeGraph;

    @Transactional
    public DocumentNode ingestDocument(String fileName, String contentType, InputStream stream) {
        log.info("Starting RAG ingestion pipeline for file: {}", fileName);
        
        // 1. Create document node record
        DocumentNode document = DocumentNode.builder()
                .fileName(fileName)
                .contentType(contentType)
                .processingStatus("PROCESSING")
                .build();
        document = documentRepository.save(document);

        try {
            // 2. Parse text content
            String parsedText = documentParser.parse(stream);
            document.setParsedText(parsedText);

            // 3. Generate chunks
            List<String> textChunks = chunkGenerator.generateChunks(parsedText, 500, 100);
            log.info("Generated {} chunks for file: {}", textChunks.size(), fileName);

            // 4. Compute embeddings and index in vector registry
            for (int i = 0; i < textChunks.size(); i++) {
                String chunkText = textChunks.get(i);
                double[] vector = embeddingService.generateEmbedding(chunkText);
                
                ChunkNode chunkNode = ChunkNode.builder()
                        .document(document)
                        .chunkIndex(i)
                        .content(chunkText)
                        .build();
                        
                vectorStore.store(chunkNode, vector);
            }

            // 5. Extract entity relationships for Knowledge Graph
            knowledgeGraph.extractRelations(document, parsedText);

            // 6. Complete status updates
            document.setProcessingStatus("COMPLETED");
            log.info("RAG ingestion pipeline completed successfully for file: {}", fileName);
        } catch (Exception e) {
            log.error("RAG pipeline failed for file: {}", fileName, e);
            document.setProcessingStatus("FAILED");
        }

        return documentRepository.save(document);
    }
}
