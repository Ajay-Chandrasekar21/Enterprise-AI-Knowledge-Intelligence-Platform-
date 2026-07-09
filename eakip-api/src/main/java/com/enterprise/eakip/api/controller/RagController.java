package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.rag.model.DocumentNode;
import com.enterprise.eakip.rag.model.GraphRelation;
import com.enterprise.eakip.rag.repository.DocumentNodeRepository;
import com.enterprise.eakip.rag.repository.GraphRelationRepository;
import com.enterprise.eakip.rag.vector.RagPipelineService;
import com.enterprise.eakip.rag.vector.VectorStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
@Tag(name = "Knowledge RAG API", description = "Endpoints for document uploads, vector semantic searches, and Knowledge Graphs")
public class RagController {

    private final RagPipelineService pipelineService;
    private final VectorStoreService vectorStore;
    private final DocumentNodeRepository documentRepository;
    private final GraphRelationRepository relationRepository;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(summary = "Upload document for semantic indexing", description = "Parses PDF, DOCX, TXT, PPTX or Markdown files, runs sliding window chunk splitting, and generates embeddings")
    public ResponseEntity<ApiResponse<DocumentNode>> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            DocumentNode result = pipelineService.ingestDocument(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getInputStream()
            );
            return ResponseEntity.ok(ApiResponse.success("Document uploaded and queued for processing", result));
        } catch (Exception e) {
            log.error("Upload failed", e);
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "Upload parsing failed: " + e.getMessage(), null, java.time.LocalDateTime.now()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Semantic vector search", description = "Computes text embeddings and performs cosine-similarity checks across document chunks")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {
            
        List<VectorStoreService.SearchResult> searchResults = vectorStore.search(query, topK);
        List<Map<String, Object>> response = new ArrayList<>();
        
        for (VectorStoreService.SearchResult res : searchResults) {
            Map<String, Object> map = new HashMap<>();
            map.put("chunkId", res.getChunkNode().getId());
            map.put("content", res.getChunkNode().getContent());
            map.put("score", res.getScore());
            map.put("sourceFile", res.getChunkNode().getDocument().getFileName());
            response.add(map);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Semantic search completed", response));
    }

    @GetMapping("/documents")
    @Operation(summary = "List indexed documents", description = "Queries parsed document indexes and processing statuses")
    public ResponseEntity<ApiResponse<List<DocumentNode>>> getDocuments() {
        return ResponseEntity.ok(ApiResponse.success("Documents loaded", documentRepository.findAll()));
    }

    @GetMapping("/graph")
    @Operation(summary = "Retrieve Knowledge Graph relationship mappings", description = "Exposes parsed nodes and edges representing entity relationships")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGraph() {
        List<GraphRelation> relations = relationRepository.findAll();
        
        Set<Map<String, String>> nodes = new HashSet<>();
        List<Map<String, Object>> edges = new ArrayList<>();

        for (GraphRelation rel : relations) {
            // Register source node
            Map<String, String> sourceNode = new HashMap<>();
            sourceNode.put("id", rel.getSourceEntity());
            sourceNode.put("label", rel.getSourceEntity());
            nodes.add(sourceNode);

            // Register target node
            Map<String, String> targetNode = new HashMap<>();
            targetNode.put("id", rel.getTargetEntity());
            targetNode.put("label", rel.getTargetEntity());
            nodes.add(targetNode);

            // Register edge link
            Map<String, Object> edge = new HashMap<>();
            edge.put("source", rel.getSourceEntity());
            edge.put("target", rel.getTargetEntity());
            edge.put("type", rel.getRelationType());
            edge.put("weight", rel.getConfidence());
            edges.add(edge);
        }

        Map<String, Object> graph = new HashMap<>();
        graph.put("nodes", nodes);
        graph.put("edges", edges);

        return ResponseEntity.ok(ApiResponse.success("Knowledge Graph loaded", graph));
    }
}
