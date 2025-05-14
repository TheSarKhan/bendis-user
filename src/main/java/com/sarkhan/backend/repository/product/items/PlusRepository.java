package com.sarkhan.backend.repository.product.items;

import com.sarkhan.backend.model.product.items.Plus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlusRepository extends JpaRepository<Plus, Long> {
    Optional<Plus> findByHeader(String header);
}
