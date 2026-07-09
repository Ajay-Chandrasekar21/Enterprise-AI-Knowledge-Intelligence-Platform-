package com.enterprise.eakip.rag.repository;

import com.enterprise.eakip.rag.model.GraphRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GraphRelationRepository extends JpaRepository<GraphRelation, UUID> {
    List<GraphRelation> findByDocumentId(UUID documentId);
    void deleteByDocumentId(UUID documentId);
}
