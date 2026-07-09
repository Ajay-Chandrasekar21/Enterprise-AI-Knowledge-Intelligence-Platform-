package com.enterprise.eakip.rag.model;

import com.enterprise.eakip.core.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "graph_relations")
public class GraphRelation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private DocumentNode document;

    @Column(name = "source_entity", nullable = false, length = 150)
    private String sourceEntity;

    @Column(name = "relation_type", nullable = false, length = 100)
    private String relationType;

    @Column(name = "target_entity", nullable = false, length = 150)
    private String targetEntity;

    @Column(name = "confidence", nullable = false)
    @Builder.Default
    private Double confidence = 1.0;
}
