package com.enterprise.eakip.agent.ai.workflow;

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

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workflow_instances")
public class WorkflowInstance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "definition_id", nullable = false)
    private WorkflowDefinition definition;

    @Column(name = "current_node_id", length = 100)
    private String currentNodeId;

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "RUNNING"; // RUNNING, COMPLETED, WAITING_APPROVAL, FAILED

    @Column(name = "variables_json", columnDefinition = "text")
    private String variablesJson; // execution state context

    @Column(name = "error_message", length = 255)
    private String errorMessage;

    @Column(name = "last_updated", nullable = false)
    @Builder.Default
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
