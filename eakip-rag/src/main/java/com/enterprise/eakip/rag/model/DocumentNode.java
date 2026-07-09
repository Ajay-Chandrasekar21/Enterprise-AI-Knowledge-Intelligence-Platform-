package com.enterprise.eakip.rag.model;

import com.enterprise.eakip.core.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_nodes")
public class DocumentNode extends BaseEntity {

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "parsed_text", columnDefinition = "text")
    private String parsedText;

    @Column(name = "processing_status", nullable = false, length = 50)
    @Builder.Default
    private String processingStatus = "PROCESSING"; // PROCESSING, COMPLETED, FAILED
}
