package com.sarkhan.backend.repository.product.items;

import com.sarkhan.backend.model.product.items.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query(value = "select * from categories where name % :name", nativeQuery = true)
    List<Category> searchByName(String name);
}
