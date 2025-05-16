package com.sarkhan.backend.repository.product;

import com.sarkhan.backend.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {
    Optional<Product> getBySlug(String slug);

    @Query(value = "SELECT * FROM products WHERE name % :name", nativeQuery = true)
    List<Product> searchByName(@Param("name") String name);

    List<Product> getBySubCategoryId(Long subCategoryId);

    List<Product> getBySellerId(Long sellerId);
}
