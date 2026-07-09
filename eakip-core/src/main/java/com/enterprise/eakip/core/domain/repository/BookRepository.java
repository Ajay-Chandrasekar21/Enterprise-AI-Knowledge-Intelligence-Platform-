package com.enterprise.eakip.core.domain.repository;

import com.enterprise.eakip.core.domain.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
}
