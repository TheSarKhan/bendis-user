package com.sarkhan.backend.repository.product.items;

import com.sarkhan.backend.model.product.items.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findByName(String name);

    @Query(value = "SELECT * FROM sub_categories WHERE name % :name", nativeQuery = true)
    List<SubCategory> searchByName(@Param("name") String name);

    List<SubCategory> getByCategoryId(Long id);

    @Query("select categoryId from SubCategory where id in :subCategoryIds")
    List<Long> getCategoryIdsBySubCategoryIds(List<Long> subCategoryIds);

    @Query("from SubCategory where categoryId in :categoryIds")
    List<SubCategory> getByCategoryIds(List<Long> categoryIds);
}
