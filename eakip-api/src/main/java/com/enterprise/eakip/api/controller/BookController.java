package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.api.dto.BookRequest;
import com.enterprise.eakip.api.dto.BookResponse;
import com.enterprise.eakip.api.mapper.DomainMapper;
import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.core.common.dto.PageResponse;
import com.enterprise.eakip.core.domain.model.Book;
import com.enterprise.eakip.core.usecase.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Book Catalog API", description = "Endpoints for search, details lookup, and librarians curation operations")
public class BookController {

    private final BookService bookService;
    private final DomainMapper domainMapper;

    @GetMapping("/search")
    @Operation(summary = "Search book catalog", description = "Retrieve list of books filtered by title and category with sorting and paging support")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
            
        Page<Book> books = bookService.searchBooks(title, categoryId, page, size, sortBy, direction);
        Page<BookResponse> responses = books.map(domainMapper::toBookResponse);
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(responses)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(summary = "Add a new book to catalog", description = "Registers book metadata and maps publisher, category, and author links")
    public ResponseEntity<ApiResponse<BookResponse>> create(@Valid @RequestBody BookRequest request) {
        Book book = Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .edition(request.getEdition())
                .language(request.getLanguage())
                .shelf(request.getShelf())
                .rack(request.getRack())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .build();
                
        Book savedBook = bookService.createBook(book, request.getAuthorIds(), request.getPublisherId(), request.getCategoryId());
        return new ResponseEntity<>(ApiResponse.success("Book created successfully", domainMapper.toBookResponse(savedBook)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(summary = "Edit catalog book details", description = "Updates metadata properties and external relations tags")
    public ResponseEntity<ApiResponse<BookResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody BookRequest request) {
            
        Book bookDetails = Book.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .edition(request.getEdition())
                .language(request.getLanguage())
                .shelf(request.getShelf())
                .rack(request.getRack())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies()) // Simply matches for stub updates
                .build();
                
        Book updatedBook = bookService.updateBook(id, bookDetails, request.getAuthorIds(), request.getPublisherId(), request.getCategoryId());
        return ResponseEntity.ok(ApiResponse.success("Book updated successfully", domainMapper.toBookResponse(updatedBook)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(summary = "Delete book from repository", description = "Triggers soft deletion marking 'deleted = true' on the record")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.emptySuccess("Book deleted successfully"));
    }
}
