package com.enterprise.eakip.core.usecase.service;

import com.enterprise.eakip.core.domain.exception.EntityNotFoundException;
import com.enterprise.eakip.core.domain.exception.ResourceConflictException;
import com.enterprise.eakip.core.domain.model.Book;
import com.enterprise.eakip.core.domain.model.BookReview;
import com.enterprise.eakip.core.domain.model.ReadingSession;
import com.enterprise.eakip.core.domain.model.User;
import com.enterprise.eakip.core.domain.repository.BookRepository;
import com.enterprise.eakip.core.domain.repository.BookReviewRepository;
import com.enterprise.eakip.core.domain.repository.ReadingSessionRepository;
import com.enterprise.eakip.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadingHistoryService {

    private final ReadingSessionRepository readingSessionRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReadingSession startReadingSession(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        // Check if there is an active session already
        boolean activeExists = readingSessionRepository.findByUserIdAndBookIdAndEndTimeIsNull(userId, bookId).isPresent();
        if (activeExists) {
            throw new ResourceConflictException("You already have an active reading session for this book");
        }

        ReadingSession session = ReadingSession.builder()
                .user(user)
                .book(book)
                .startTime(LocalDateTime.now())
                .build();

        return readingSessionRepository.save(session);
    }

    @Transactional
    public ReadingSession endReadingSession(UUID userId, UUID bookId, int pagesRead, int progressPercentage) {
        ReadingSession session = readingSessionRepository.findByUserIdAndBookIdAndEndTimeIsNull(userId, bookId)
                .orElseThrow(() -> new EntityNotFoundException("Active reading session not found"));

        session.setEndTime(LocalDateTime.now());
        session.setPagesRead(pagesRead);
        session.setProgressPercentage(progressPercentage);

        return readingSessionRepository.save(session);
    }

    @Transactional
    public BookReview addReview(UUID userId, UUID bookId, int rating, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        BookReview review = BookReview.builder()
                .user(user)
                .book(book)
                .rating(rating)
                .comment(comment)
                .reviewDate(LocalDateTime.now())
                .build();

        return bookReviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReadingSession> getSessions(UUID userId) {
        return readingSessionRepository.findByUserId(userId);
    }
}
