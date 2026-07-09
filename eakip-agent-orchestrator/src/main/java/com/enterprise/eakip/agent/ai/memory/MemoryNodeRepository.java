package com.enterprise.eakip.agent.ai.memory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemoryNodeRepository extends JpaRepository<MemoryNode, UUID> {
    List<MemoryNode> findByUserId(UUID userId);
    List<MemoryNode> findByUserIdAndMemoryType(UUID userId, String memoryType);

    @Query("SELECT m FROM MemoryNode m WHERE m.userId = :userId AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MemoryNode> searchMemories(@Param("userId") UUID userId, @Param("query") String query);
}
