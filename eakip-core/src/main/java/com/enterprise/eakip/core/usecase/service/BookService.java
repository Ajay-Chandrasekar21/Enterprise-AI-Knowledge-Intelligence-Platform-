package com.enterprise.eakip.core.usecase.service;

import com.enterprise.eakip.core.domain.exception.EntityNotFoundException;
import com.enterprise.eakip.core.domain.exception.ResourceConflictException;
import com.enterprise.eakip.core.domain.model.Author;
import com.enterprise.eakip.core.domain.model.Book;
import com.enterprise.eakip.core.domain.model.Category;
import com.enterprise.eakip.core.domain.model.Publisher;
import com.enterprise.eakip.core.domain.repository.AuthorRepository;
import com.enterprise.eakip.core.domain.repository.BookRepository;
import com.enterprise.eakip.core.domain.repository.CategoryRepository;
import com.enterprise.eakip.core.domain.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<Book> searchBooks(String title, UUID categoryId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (title != null && !title.trim().isEmpty()) {
            return bookRepository.findAll(pageable); // Can be replaced with specific search specs
        }
        return bookRepository.findAll(pageable);
    }

    @Transactional
    public Book createBook(Book book, List<UUID> authorIds, UUID publisherId, UUID categoryId) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new ResourceConflictException("Book with ISBN " + book.getIsbn() + " already exists");
        }

        if (publisherId != null) {
            Publisher publisher = publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new EntityNotFoundException("Publisher not found"));
            book.setPublisher(publisher);
        }

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            book.setCategory(category);
        }

        if (authorIds != null && !authorIds.isEmpty()) {
            Set<Author> authors = new HashSet<>(authorRepository.findAllById(authorIds));
            book.setAuthors(authors);
        }

        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(UUID bookId, Book bookDetails, List<UUID> authorIds, UUID publisherId, UUID categoryId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        book.setTitle(bookDetails.getTitle());
        book.setDescription(bookDetails.getDescription());
        book.setEdition(bookDetails.getEdition());
        book.setLanguage(bookDetails.getLanguage());
        book.setShelf(bookDetails.getShelf());
        book.setRack(bookDetails.getRack());
        book.setTotalCopies(bookDetails.getTotalCopies());
        book.setAvailableCopies(bookDetails.getAvailableCopies());

        if (publisherId != null) {
            Publisher publisher = publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new EntityNotFoundException("Publisher not found"));
            book.setPublisher(publisher);
        }

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            book.setCategory(category);
        }

        if (authorIds != null) {
            Set<Author> authors = new HashSet<>(authorRepository.findAllById(authorIds));
            book.setAuthors(authors);
        }

        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        bookRepository.delete(book); // Hibernate soft delete kicks in
    }
}
