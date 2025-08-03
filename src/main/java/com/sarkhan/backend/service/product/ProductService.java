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

    List<ProductResponseForGroupOfProduct> getAllFamousProducts();

    List<ProductResponseForGroupOfProduct> getAllDiscountedProducts();

    List<ProductResponseForGroupOfProduct> getAllMostFavoriteProducts();

    List<ProductResponseForGroupOfProduct> getAllFlushProducts();

    List<ProductResponseForGroupOfProduct> getAllRecommendedProduct();

    ProductResponseForGetSingleOne getByIdAndAddHistory(Long id);

    ProductResponseForGetSingleOne getBySlug(String slug);

    CompletableFuture<ProductResponseForSearchByName> searchByName(String name);

    ProductResponseForSelectedSubCategoryAndComplexFilter getBySubCategoryId(Long subCategoryId);

    List<ProductResponseForGroupOfProduct> getBySellerId(Long sellerId);

    Product getById(Long id);

    CompletableFuture<ProductResponseForSelectedSubCategoryAndComplexFilter> getByComplexFiltering(ProductFilterRequest request);

    List<Product> getAllFavorite() throws AuthException;

    void giveRating(Long id, Double rating) throws AuthException;

    ProductResponseForGetSingleOne toggleFavorite(Long id) throws AuthException;

    ProductResponseForGetSingleOne update(Long id, ProductRequest request, List<MultipartFile> newImages) throws IOException, AuthException;

    void delete(Long id) throws AuthException;
}
