package com.enterprise.eakip.agent.ai.memory;

import com.enterprise.eakip.core.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "memory_nodes")
public class MemoryNode extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "memory_type", nullable = false, length = 50)
    private String memoryType; // EPISODIC, SEMANTIC, PROCEDURAL, PREFERENCE, WORKFLOW

    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "relevance_score", nullable = false)
    @Builder.Default
    private Double relevanceScore = 1.0;

    @Column(name = "created_date", nullable = false)
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_accessed_date")
    @Builder.Default
    private LocalDateTime lastAccessedDate = LocalDateTime.now();

    @Column(name = "retention_days", nullable = false)
    @Builder.Default
    private Integer retentionDays = 30; // Expiration policy
}
