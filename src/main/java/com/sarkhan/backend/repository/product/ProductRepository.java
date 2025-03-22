package com.sarkhan.backend.repository.product;

import com.sarkhan.backend.model.product.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.slug = :slug AND p.id = :productId")
    Optional<Product> findByBrandAndSlugAndId(@Param("brand") String brand,
                                              @Param("slug") String slug,
                                              @Param("productId") Long productId);
}
