package com.enterprise.eakip.agent.ai.workflow;

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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workflow_definitions")
public class WorkflowDefinition extends BaseEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "workflow_version", nullable = false)
    @Builder.Default
    private Integer workflowVersion = 1;

    @Column(name = "trigger_type", nullable = false, length = 50)
    private String triggerType; // BOOK_UPLOADED, BORROW_CREATED, DOCUMENT_UPLOADED, MANUAL

    @Column(name = "nodes_json", columnDefinition = "text", nullable = false)
    private String nodesJson; // Node definitions schema config

    @Column(name = "created_date", nullable = false)
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();
}
