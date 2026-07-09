package com.enterprise.eakip.core.domain.repository;

import com.enterprise.eakip.core.domain.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, UUID> {
    Optional<Publisher> findByName(String name);
}
