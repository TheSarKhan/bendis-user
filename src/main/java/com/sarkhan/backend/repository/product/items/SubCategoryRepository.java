package com.sarkhan.backend.repository.product.items;

import com.sarkhan.backend.model.product.items.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findByName(String name);

    @Query("from sub_categories where name ilike '%' || :name || '%'")
    List<SubCategory> searchByName(String name);

    List<SubCategory> getByCategoryId(Long id);
}
