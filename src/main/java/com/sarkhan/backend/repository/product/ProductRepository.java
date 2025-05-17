package com.sarkhan.backend.repository.product;

import com.sarkhan.backend.model.product.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {
    Optional<Product> getBySlug(String slug);

    @Query("from products where name ilike '%' || :name || '%'")
    List<Product> searchByName(String name);

    List<Product> getBySubCategoryId(Long subCategoryId);

    List<Product> getBySellerId(Long sellerId);

    List<Product> findTop5BySellerIdOrderBySalesCountDesc(Long sellerId);
}
