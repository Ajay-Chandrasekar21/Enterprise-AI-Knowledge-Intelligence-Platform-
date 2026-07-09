package com.enterprise.eakip.core.usecase.service;

import com.enterprise.eakip.core.domain.exception.EntityNotFoundException;
import com.enterprise.eakip.core.domain.exception.ResourceConflictException;
import com.enterprise.eakip.core.domain.model.Book;
import com.enterprise.eakip.core.domain.model.Reservation;
import com.enterprise.eakip.core.domain.model.ReservationStatus;
import com.enterprise.eakip.core.domain.model.User;
import com.enterprise.eakip.core.domain.repository.BookRepository;
import com.enterprise.eakip.core.domain.repository.ReservationRepository;
import com.enterprise.eakip.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public Reservation reserveBook(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        // If copies are available, throw exception (should borrow instead)
        if (book.getAvailableCopies() > 0) {
            throw new ResourceConflictException("Book is currently available for borrowing. Check out instead.");
        }

        // Check if user already has a pending reservation for this book
        boolean alreadyReserved = reservationRepository.findByBookIdAndUserIdAndStatus(bookId, userId, ReservationStatus.PENDING).isPresent();
        if (alreadyReserved) {
            throw new ResourceConflictException("You already have an active reservation hold for this book");
        }

        int maxQueuePosition = reservationRepository.findMaxQueuePositionByBookId(bookId);

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .reservationDate(LocalDateTime.now())
                .status(ReservationStatus.PENDING)
                .queuePosition(maxQueuePosition + 1)
                .build();

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ResourceConflictException("Cannot cancel a completed/fulfilled reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setQueuePosition(0);
        reservationRepository.save(reservation);

        // Re-number subsequent reservations queue positions
        List<Reservation> remaining = reservationRepository
                .findByBookIdAndStatusOrderByReservationDateAsc(reservation.getBook().getId(), ReservationStatus.PENDING);
        
        for (int i = 0; i < remaining.size(); i++) {
            Reservation rem = remaining.get(i);
            rem.setQueuePosition(i + 1);
            reservationRepository.save(rem);
        }
    }
}
