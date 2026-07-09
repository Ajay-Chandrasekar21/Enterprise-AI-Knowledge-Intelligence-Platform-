package com.enterprise.eakip.core.domain.repository;

import com.enterprise.eakip.core.domain.model.Borrowing;
import com.enterprise.eakip.core.domain.model.BorrowingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, UUID> {
    List<Borrowing> findByUserId(UUID userId);
    List<Borrowing> findByUserIdAndStatus(UUID userId, BorrowingStatus status);
    long countByUserIdAndStatus(UUID userId, BorrowingStatus status);
    List<Borrowing> findByBookIdAndStatus(UUID bookId, BorrowingStatus status);
    
    @Query("SELECT b FROM Borrowing b WHERE b.status = 'BORROWED' AND b.dueDate < :now")
    List<Borrowing> findOverdueBorrowings(@Param("now") LocalDateTime now);
}
