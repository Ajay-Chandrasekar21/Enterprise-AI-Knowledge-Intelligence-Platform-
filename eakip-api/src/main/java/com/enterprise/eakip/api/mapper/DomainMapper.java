package com.enterprise.eakip.api.mapper;

import com.enterprise.eakip.api.dto.BookResponse;
import com.enterprise.eakip.api.dto.BorrowResponse;
import com.enterprise.eakip.api.dto.ReservationResponse;
import com.enterprise.eakip.core.domain.model.Author;
import com.enterprise.eakip.core.domain.model.Book;
import com.enterprise.eakip.core.domain.model.Borrowing;
import com.enterprise.eakip.core.domain.model.Reservation;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DomainMapper {

    public BookResponse toBookResponse(Book book) {
        if (book == null) return null;
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .publisherName(book.getPublisher() != null ? book.getPublisher().getName() : null)
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
                .authorNames(book.getAuthors() != null 
                        ? book.getAuthors().stream().map(Author::getName).collect(Collectors.toList()) 
                        : null)
                .edition(book.getEdition())
                .language(book.getLanguage())
                .shelf(book.getShelf())
                .rack(book.getRack())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }

    public BorrowResponse toBorrowResponse(Borrowing borrowing) {
        if (borrowing == null) return null;
        return BorrowResponse.builder()
                .id(borrowing.getId())
                .username(borrowing.getUser().getUsername())
                .bookTitle(borrowing.getBook().getTitle())
                .isbn(borrowing.getBook().getIsbn())
                .borrowDate(borrowing.getBorrowDate())
                .dueDate(borrowing.getDueDate())
                .returnDate(borrowing.getReturnDate())
                .status(borrowing.getStatus().name())
                .renewalCount(borrowing.getRenewalCount())
                .build();
    }

    public ReservationResponse toReservationResponse(Reservation reservation) {
        if (reservation == null) return null;
        return ReservationResponse.builder()
                .id(reservation.getId())
                .username(reservation.getUser().getUsername())
                .bookTitle(reservation.getBook().getTitle())
                .reservationDate(reservation.getReservationDate())
                .status(reservation.getStatus().name())
                .queuePosition(reservation.getQueuePosition())
                .build();
    }
}
