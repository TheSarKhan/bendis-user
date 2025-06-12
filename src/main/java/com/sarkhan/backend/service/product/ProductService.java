package com.sarkhan.backend.service.product;

import com.sarkhan.backend.dto.product.*;
import com.sarkhan.backend.model.product.Product;
import jakarta.security.auth.message.AuthException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    CompletableFuture<Product> add(ProductRequest request, List<MultipartFile> images) throws IOException, AuthException;

    List<Product> getAll();

    ProductResponseForHomePage getForHomePage();

    Product getByIdAndAddHistory(Long id);

    Product getBySlug(String slug);

    CompletableFuture<ProductResponseForSearchByName> searchByName(String name);

    CompletableFuture<ProductResponseForSelectedSubCategory> getBySubCategoryId(Long subCategoryId);

    ProductResponseForGetBySellerId getBySellerId(Long sellerId);

    CompletableFuture<ProductResponseForSelectedSubCategory> getByComplexFiltering(ProductFilterRequest request);

    List<Product> getAllFavorite() throws AuthException;

    Product giveRating(Long id, Double rating) throws AuthException;

    Product toggleFavorite(Long id) throws AuthException;

    Product update(Long id, ProductRequest request, List<MultipartFile> newImages) throws IOException, AuthException;

    void delete(Long id) throws AuthException;
}
