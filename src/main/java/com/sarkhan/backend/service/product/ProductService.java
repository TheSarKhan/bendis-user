package com.sarkhan.backend.service.product;

import com.sarkhan.backend.dto.product.*;
import com.sarkhan.backend.model.product.Product;
import jakarta.security.auth.message.AuthException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    Product add(ProductRequest request, List<MultipartFile> images) throws IOException, AuthException;

    List<Product> getAll();

    ProductResponseForHomePage getForHomePage();

    ProductResponseSimple getAllFamousProducts();

    ProductResponseSimple getAllDiscountedProducts();

    ProductResponseSimple getAllMostFavoriteProducts();

    ProductResponseSimple getAllFlushProducts();

    ProductResponseSimple getAllRecommendedProduct();

    ProductResponseForGetSingleOne getByIdAndAddHistory(Long id);

    ProductResponseForGetSingleOne getBySlug(String slug);

    CompletableFuture<ProductResponseForSearchByName> searchByName(String name);

    CompletableFuture<ProductResponseForSelectedSubCategoryAndComplexFilter> getBySubCategoryId(Long subCategoryId);

    ProductResponseForGetBySellerId getBySellerId(Long sellerId);

    Product getById(Long id);

    CompletableFuture<ProductResponseForSelectedSubCategoryAndComplexFilter> getByComplexFiltering(ProductFilterRequest request);

    List<Product> getAllFavorite() throws AuthException;

    void giveRating(Long id, Double rating) throws AuthException;

    ProductResponseForGetSingleOne toggleFavorite(Long id) throws AuthException;

    Product update(Long id, ProductRequest request, List<MultipartFile> newImages) throws IOException, AuthException;

    void delete(Long id) throws AuthException;
}
