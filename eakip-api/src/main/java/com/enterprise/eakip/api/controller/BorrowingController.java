package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.api.dto.BorrowResponse;
import com.enterprise.eakip.api.mapper.DomainMapper;
import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.core.domain.model.Borrowing;
import com.enterprise.eakip.core.usecase.service.BorrowingService;
import com.enterprise.eakip.security.service.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/borrowings")
@RequiredArgsConstructor
@Tag(name = "Borrowing Operations API", description = "Endpoints for borrowing, returning, and renewing books")
public class BorrowingController {

    private final BorrowingService borrowingService;
    private final DomainMapper domainMapper;

    @PostMapping("/books/{bookId}/borrow")
    @Operation(summary = "Borrow a book copy", description = "Checks role limits and availability. Decrements stock and initializes checkout timeframes")
    public ResponseEntity<ApiResponse<BorrowResponse>> borrowBook(
            @PathVariable UUID bookId,
            @AuthenticationPrincipal UserPrincipal principal) {
            
        Borrowing borrowing = borrowingService.borrowBook(principal.getId(), bookId);
        return ResponseEntity.ok(ApiResponse.success("Book borrowed successfully", domainMapper.toBorrowResponse(borrowing)));
    }

    @PostMapping("/{borrowingId}/return")
    @Operation(summary = "Return borrowed book", description = "Marks return dates and checks for late return fines. Restores availability index")
    public ResponseEntity<ApiResponse<BorrowResponse>> returnBook(@PathVariable UUID borrowingId) {
        Borrowing borrowing = borrowingService.returnBook(borrowingId);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", domainMapper.toBorrowResponse(borrowing)));
    }

    @PostMapping("/{borrowingId}/renew")
    @Operation(summary = "Renew active borrowing checkout", description = "Extends the due date by 14 days, verifying max renewal limits")
    public ResponseEntity<ApiResponse<BorrowResponse>> renewBook(@PathVariable UUID borrowingId) {
        Borrowing borrowing = borrowingService.renewBook(borrowingId);
        return ResponseEntity.ok(ApiResponse.success("Book renewed successfully", domainMapper.toBorrowResponse(borrowing)));
    }
}
