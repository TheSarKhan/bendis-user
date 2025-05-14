package com.sarkhan.backend.repository.product.items;

import com.sarkhan.backend.model.product.items.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);

    Optional<Category> findByName(String name);

    @Query("from categories where name ilike '%' || :name || '%' ")
    List<Category> searchByName(String Name);
}
