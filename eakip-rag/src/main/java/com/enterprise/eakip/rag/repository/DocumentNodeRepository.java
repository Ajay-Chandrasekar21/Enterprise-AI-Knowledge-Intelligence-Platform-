package com.enterprise.eakip.rag.repository;

import com.enterprise.eakip.rag.model.DocumentNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentNodeRepository extends JpaRepository<DocumentNode, UUID> {
}
