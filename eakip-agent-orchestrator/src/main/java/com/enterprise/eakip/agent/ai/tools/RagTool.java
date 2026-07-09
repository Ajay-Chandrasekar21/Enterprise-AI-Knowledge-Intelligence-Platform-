package com.enterprise.eakip.agent.ai.tools;

import com.enterprise.eakip.rag.vector.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RagTool implements Tool {

    private final VectorStoreService vectorStore;

    @Override
    public String getName() {
        return "RAGDocumentSearchTool";
    }

    @Override
    public String getDescription() {
        return "Retrieves parsed sliding window chunks matching semantic queries using cosine-similarity checks";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("query", "The semantic query string instruction");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String query = (String) arguments.get("query");
        List<VectorStoreService.SearchResult> searchResults = vectorStore.search(query, 3);
        
        return searchResults.stream()
                .map(res -> Map.of(
                        "content", res.getChunkNode().getContent(),
                        "score", res.getScore(),
                        "sourceFile", res.getChunkNode().getDocument().getFileName()
                ))
                .collect(Collectors.toList());
    }
}
