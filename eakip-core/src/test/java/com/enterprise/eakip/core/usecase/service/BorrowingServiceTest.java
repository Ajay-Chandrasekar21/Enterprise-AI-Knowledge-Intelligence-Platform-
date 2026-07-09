package com.enterprise.eakip.core.usecase.service;

import com.enterprise.eakip.core.domain.exception.ResourceConflictException;
import com.enterprise.eakip.core.domain.model.Book;
import com.enterprise.eakip.core.domain.model.Borrowing;
import com.enterprise.eakip.core.domain.model.BorrowingStatus;
import com.enterprise.eakip.core.domain.model.Role;
import com.enterprise.eakip.core.domain.model.User;
import com.enterprise.eakip.core.domain.repository.BookRepository;
import com.enterprise.eakip.core.domain.repository.BorrowingRepository;
import com.enterprise.eakip.core.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BorrowingService borrowingService;

    private User student;
    private Book availableBook;
    private Book outOfStockBook;

    @BeforeEach
    void setUp() {
        student = User.builder()
                .username("teststudent")
                .role(Role.STUDENT)
                .build();
        student.setId(UUID.randomUUID());

        availableBook = Book.builder()
                .title("Clean Code")
                .isbn("978-0132350884")
                .totalCopies(5)
                .availableCopies(3)
                .build();
        availableBook.setId(UUID.randomUUID());

        outOfStockBook = Book.builder()
                .title("Clean Architecture")
                .isbn("978-0134494166")
                .totalCopies(3)
                .availableCopies(0)
                .build();
        outOfStockBook.setId(UUID.randomUUID());
    }

    @Test
    void borrowBook_Success() {
        // Arrange
        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(bookRepository.findById(availableBook.getId())).thenReturn(Optional.of(availableBook));
        when(borrowingRepository.countByUserIdAndStatus(student.getId(), BorrowingStatus.BORROWED)).thenReturn(0L);

        Borrowing mockBorrowing = Borrowing.builder()
                .user(student)
                .book(availableBook)
                .status(BorrowingStatus.BORROWED)
                .build();
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(mockBorrowing);

        // Act
        Borrowing result = borrowingService.borrowBook(student.getId(), availableBook.getId());

        // Assert
        assertNotNull(result);
        verify(bookRepository).save(availableBook);
        verify(borrowingRepository).save(any(Borrowing.class));
    }

    @Test
    void borrowBook_ThrowsConflictException_WhenStudentLimitReached() {
        // Arrange
        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(bookRepository.findById(availableBook.getId())).thenReturn(Optional.of(availableBook));
        when(borrowingRepository.countByUserIdAndStatus(student.getId(), BorrowingStatus.BORROWED)).thenReturn(3L);

        // Act & Assert
        assertThrows(ResourceConflictException.class, 
                () -> borrowingService.borrowBook(student.getId(), availableBook.getId()));
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    void borrowBook_ThrowsConflictException_WhenBookOutOfStock() {
        // Arrange
        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(bookRepository.findById(outOfStockBook.getId())).thenReturn(Optional.of(outOfStockBook));
        when(borrowingRepository.countByUserIdAndStatus(student.getId(), BorrowingStatus.BORROWED)).thenReturn(0L);

        // Act & Assert
        assertThrows(ResourceConflictException.class, 
                () -> borrowingService.borrowBook(student.getId(), outOfStockBook.getId()));
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }
}
