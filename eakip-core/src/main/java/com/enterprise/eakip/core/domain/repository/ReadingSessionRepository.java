package com.enterprise.eakip.core.domain.repository;

import com.enterprise.eakip.core.domain.model.ReadingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingSessionRepository extends JpaRepository<ReadingSession, UUID> {
    List<ReadingSession> findByUserId(UUID userId);
    Optional<ReadingSession> findByUserIdAndBookIdAndEndTimeIsNull(UUID userId, UUID bookId);
}
