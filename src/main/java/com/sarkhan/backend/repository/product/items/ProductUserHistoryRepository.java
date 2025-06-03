package com.sarkhan.backend.repository.product.items;

import com.sarkhan.backend.model.product.items.ProductUserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUserHistoryRepository extends JpaRepository<ProductUserHistory, Long> {
    @Query("select subCategoryId from product_user_histories where userId = :userId group by subCategoryId order by COUNT(*) DESC")
    List<Long> findTopSubCategoryIdsByUserId(Long userId);
}
