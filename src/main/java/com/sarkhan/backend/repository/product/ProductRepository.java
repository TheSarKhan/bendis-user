package com.sarkhan.backend.repository.product;

import com.sarkhan.backend.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> getBySlug(String slug);

    List<Product> getBySubCategoryId(Long subCategoryId);

    List<Product> getBySellerId(Long sellerId);

    @Query("from Product where discountedPrice is not null order by updateAt desc limit 20")
    List<Product> getDiscountedProducts();

    @Query("from Product order by favoriteCount desc limit 20")
    List<Product> getMostFavoriteProducts();

    @Query("""
           select p from Product p
           join UserFavoriteProduct f
           on p.id = f.productId
           where f.userId = :userId
           """)
    List<Product> getAllFavorite(Long userId);
}
