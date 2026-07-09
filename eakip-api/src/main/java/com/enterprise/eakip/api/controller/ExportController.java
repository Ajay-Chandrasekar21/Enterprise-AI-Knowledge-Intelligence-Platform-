package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.core.usecase.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports Exporter API", description = "Endpoints for downloading platform CSV audits data (Librarian/Admin only)")
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/books/csv")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(summary = "Export catalog index to CSV", description = "Compiles all non-deleted book records to a comma-separated values stream")
    public ResponseEntity<byte[]> exportBooks() {
        String csv = exportService.exportBooksToCsv();
        byte[] bytes = csv.getBytes();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books_catalog_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @GetMapping("/borrowings/csv")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(summary = "Export borrowings logs to CSV", description = "Compiles all platform transactional checkout histories to CSV")
    public ResponseEntity<byte[]> exportBorrowings() {
        String csv = exportService.exportBorrowingsToCsv();
        byte[] bytes = csv.getBytes();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=borrowings_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}
