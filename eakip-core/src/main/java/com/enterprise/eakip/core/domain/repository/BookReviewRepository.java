package com.enterprise.eakip.core.domain.repository;

import com.enterprise.eakip.core.domain.model.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, UUID> {
    List<BookReview> findByBookId(UUID bookId);
    
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM BookReview r WHERE r.book.id = :bookId")
    double findAverageRatingByBookId(@Param("bookId") UUID bookId);
}
