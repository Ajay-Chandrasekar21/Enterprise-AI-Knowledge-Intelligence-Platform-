package com.enterprise.eakip.agent.ai.tools;

import com.enterprise.eakip.core.usecase.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BookTool implements Tool {

    private final BookService bookService;

    @Override
    public String getName() {
        return "BookCatalogSearchTool";
    }

    @Override
    public String getDescription() {
        return "Queries catalog index using title filters and returns matching book entities";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("title", "The text query title matching parameters");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String title = (String) arguments.get("title");
        return bookService.searchBooks(title, null, 0, 5, "title", "ASC").getContent();
    }
}
