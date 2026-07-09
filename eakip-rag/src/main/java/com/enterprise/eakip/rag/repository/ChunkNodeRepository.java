package com.enterprise.eakip.rag.repository;

import com.enterprise.eakip.rag.model.ChunkNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChunkNodeRepository extends JpaRepository<ChunkNode, UUID> {
    List<ChunkNode> findByDocumentId(UUID documentId);
    void deleteByDocumentId(UUID documentId);

    @Query("SELECT c FROM ChunkNode c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<ChunkNode> searchByKeyword(@Param("query") String query);
}
