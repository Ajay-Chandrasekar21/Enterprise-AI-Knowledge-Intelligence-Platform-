package com.enterprise.eakip.agent.ai.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiContext {
    private String username;
    private String userRole;
    private String department;
    
    @Builder.Default
    private List<BookMetadata> activeBooks = new ArrayList<>();
    
    @Builder.Default
    private List<BorrowLog> borrowHistory = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookMetadata {
        private String bookId;
        private String title;
        private String isbn;
        private String category;
        private Boolean available;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BorrowLog {
        private String borrowId;
        private String bookTitle;
        private String dueDate;
        private String status;
    }
}
