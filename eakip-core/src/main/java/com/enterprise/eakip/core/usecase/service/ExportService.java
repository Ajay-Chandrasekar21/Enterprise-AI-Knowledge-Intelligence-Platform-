package com.enterprise.eakip.core.usecase.service;

import com.enterprise.eakip.core.domain.model.Book;
import com.enterprise.eakip.core.domain.model.Borrowing;
import com.enterprise.eakip.core.domain.repository.BookRepository;
import com.enterprise.eakip.core.domain.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;

    public String exportBooksToCsv() {
        List<Book> books = bookRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,ISBN,Title,Category,Available Copies,Total Copies\n");
        for (Book book : books) {
            csv.append(book.getId().toString()).append(",")
               .append(escapeCsv(book.getIsbn())).append(",")
               .append(escapeCsv(book.getTitle())).append(",")
               .append(book.getCategory() != null ? escapeCsv(book.getCategory().getName()) : "").append(",")
               .append(book.getAvailableCopies()).append(",")
               .append(book.getTotalCopies()).append("\n");
        }
        return csv.toString();
    }

    public String exportBorrowingsToCsv() {
        List<Borrowing> borrowings = borrowingRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("Borrow ID,User,Book,Borrow Date,Due Date,Status\n");
        for (Borrowing borrowing : borrowings) {
            csv.append(borrowing.getId().toString()).append(",")
               .append(escapeCsv(borrowing.getUser().getUsername())).append(",")
               .append(escapeCsv(borrowing.getBook().getTitle())).append(",")
               .append(borrowing.getBorrowDate().toString()).append(",")
               .append(borrowing.getDueDate().toString()).append(",")
               .append(borrowing.getStatus().name()).append("\n");
        }
        return csv.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
