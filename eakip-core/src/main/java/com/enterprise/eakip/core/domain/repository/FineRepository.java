package com.enterprise.eakip.core.domain.repository;

import com.enterprise.eakip.core.domain.model.Fine;
import com.enterprise.eakip.core.domain.model.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FineRepository extends JpaRepository<Fine, UUID> {
    Optional<Fine> findByBorrowingId(UUID borrowingId);
    
    @Query("SELECT f FROM Fine f JOIN f.borrowing b WHERE b.user.id = :userId AND f.status = :status")
    List<Fine> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") FineStatus status);
}
