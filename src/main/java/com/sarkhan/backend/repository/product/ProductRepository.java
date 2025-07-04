package com.sarkhan.backend.repository.product;

import com.sarkhan.backend.model.product.Product;
import org.springframework.data.domain.Pageable;
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

    List<Product> findTop5BySellerIdOrderBySalesCountDesc(Long sellerId);

    @Query("from Product order by salesCount")
    List<Product> getFamousProducts(Pageable pageable);

    @Query("from Product where discountedPrice is not null order by originalPrice - discountedPrice desc")
    List<Product> getDiscountedProducts(Pageable pageable);

    @Query("from Product order by favoriteCount desc")
    List<Product> getMostFavoriteProducts(Pageable pageable);

    @Query("from Product order by totalStock")
    List<Product> getFlushProducts(Pageable pageable);

    @Query("from Product order by salesCount")
    List<Product> getFamousProducts();

    @Query("from Product where discountedPrice is not null order by originalPrice - discountedPrice desc")
    List<Product> getDiscountedProducts();

    @Query("from Product order by favoriteCount desc")
    List<Product> getMostFavoriteProducts();

    @Query("from Product order by totalStock")
    List<Product> getFlushProducts();

    @Query("""
            select p from Product p
            join UserFavoriteProduct f
            on p.id = f.productId
            where f.userId = :userId
            """)
    List<Product> getAllFavorite(Long userId);

    @Query(value = """
            select distinct elem->>'color' as color
            from products,
                 jsonb_array_elements(color_and_sizes) as elem
            where sub_category_id = :subCategoryId
            """, nativeQuery = true)
    List<String> getDistinctColorsBySubCategoryId(Long subCategoryId);

    @Query(value = """
            select distinct jsonb_object_keys(elem->'sizeStockMap') as size
            from product,
                 jsonb_array_elements(color_and_sizes) as elem
            where sub_category_id = :subCategoryId
            """, nativeQuery = true)
    List<String> getDistinctSizesBySubCategoryId(Long subCategoryId);

}
