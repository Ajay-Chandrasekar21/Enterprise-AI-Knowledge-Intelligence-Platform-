package com.enterprise.eakip.core.usecase.service;

import com.enterprise.eakip.core.domain.exception.DomainException;
import com.enterprise.eakip.core.domain.exception.EntityNotFoundException;
import com.enterprise.eakip.core.domain.exception.ResourceConflictException;
import com.enterprise.eakip.core.domain.model.Book;
import com.enterprise.eakip.core.domain.model.Borrowing;
import com.enterprise.eakip.core.domain.model.BorrowingStatus;
import com.enterprise.eakip.core.domain.model.Fine;
import com.enterprise.eakip.core.domain.model.FineStatus;
import com.enterprise.eakip.core.domain.model.Role;
import com.enterprise.eakip.core.domain.model.User;
import com.enterprise.eakip.core.domain.repository.BookRepository;
import com.enterprise.eakip.core.domain.repository.BorrowingRepository;
import com.enterprise.eakip.core.domain.repository.FineRepository;
import com.enterprise.eakip.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final FineRepository fineRepository;

    private static final int MAX_STUDENT_BORROW = 3;
    private static final int MAX_FACULTY_BORROW = 10;
    private static final int STUDENT_BORROW_DAYS = 14;
    private static final int FACULTY_BORROW_DAYS = 90;
    private static final int MAX_RENEWALS = 2;
    private static final BigDecimal FINE_RATE_PER_DAY = new BigDecimal("1.00");

    @Transactional
    public Borrowing borrowBook(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        // 1. Verify borrowing limits by role
        long activeBorrowCount = borrowingRepository.countByUserIdAndStatus(userId, BorrowingStatus.BORROWED);
        if (user.getRole() == Role.STUDENT && activeBorrowCount >= MAX_STUDENT_BORROW) {
            throw new ResourceConflictException("Students cannot exceed " + MAX_STUDENT_BORROW + " active borrowings");
        }
        if (user.getRole() == Role.FACULTY && activeBorrowCount >= MAX_FACULTY_BORROW) {
            throw new ResourceConflictException("Faculty cannot exceed " + MAX_FACULTY_BORROW + " active borrowings");
        }

        // 2. Check copies availability
        if (book.getAvailableCopies() <= 0) {
            throw new ResourceConflictException("Book is currently unavailable. Please place a reservation.");
        }

        // 3. Decrement availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        int borrowDays = user.getRole() == Role.FACULTY ? FACULTY_BORROW_DAYS : STUDENT_BORROW_DAYS;
        LocalDateTime now = LocalDateTime.now();

        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(now)
                .dueDate(now.plusDays(borrowDays))
                .status(BorrowingStatus.BORROWED)
                .build();

        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public Borrowing returnBook(UUID borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing record not found"));

        if (borrowing.getStatus() != BorrowingStatus.BORROWED) {
            throw new ResourceConflictException("Book is already returned");
        }

        LocalDateTime now = LocalDateTime.now();
        borrowing.setReturnDate(now);
        borrowing.setStatus(BorrowingStatus.RETURNED);

        // Increment copies availability
        Book book = borrowing.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // Check if returned late to calculate fines
        if (now.isAfter(borrowing.getDueDate())) {
            long daysLate = Duration.between(borrowing.getDueDate(), now).toDays();
            if (daysLate > 0) {
                BigDecimal fineAmount = FINE_RATE_PER_DAY.multiply(new BigDecimal(daysLate));
                Fine fine = Fine.builder()
                        .borrowing(borrowing)
                        .amount(fineAmount)
                        .status(FineStatus.UNPAID)
                        .generatedDate(now)
                        .build();
                fineRepository.save(fine);
            }
        }

        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public Borrowing renewBook(UUID borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing record not found"));

        if (borrowing.getStatus() != BorrowingStatus.BORROWED) {
            throw new ResourceConflictException("Cannot renew a returned book");
        }

        if (borrowing.getRenewalCount() >= MAX_RENEWALS) {
            throw new ResourceConflictException("Exceeded maximum renewals count of " + MAX_RENEWALS);
        }

        borrowing.setRenewalCount(borrowing.getRenewalCount() + 1);
        borrowing.setDueDate(borrowing.getDueDate().plusDays(STUDENT_BORROW_DAYS));

        return borrowingRepository.save(borrowing);
    }
}
