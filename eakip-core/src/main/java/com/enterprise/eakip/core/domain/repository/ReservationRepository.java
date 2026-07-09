package com.enterprise.eakip.core.domain.repository;

import com.enterprise.eakip.core.domain.model.Reservation;
import com.enterprise.eakip.core.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByUserId(UUID userId);
    List<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status);
    List<Reservation> findByBookIdAndStatusOrderByReservationDateAsc(UUID bookId, ReservationStatus status);
    Optional<Reservation> findByBookIdAndUserIdAndStatus(UUID bookId, UUID userId, ReservationStatus status);

    @Query("SELECT COALESCE(MAX(r.queuePosition), 0) FROM Reservation r WHERE r.book.id = :bookId AND r.status = 'PENDING'")
    int findMaxQueuePositionByBookId(@Param("bookId") UUID bookId);
}
