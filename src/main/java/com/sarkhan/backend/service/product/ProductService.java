package com.sarkhan.backend.service.product;

import com.sarkhan.backend.dto.product.*;
import com.sarkhan.backend.model.product.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    CompletableFuture<Product> add(ProductRequest request, List<MultipartFile> images) throws IOException;

    ProductResponseForGetAll getAll();

    Product getById(Long id);

    Product getBySlug(String slug);

    CompletableFuture<ProductResponseForSearchByName> searchByName(String name);

    CompletableFuture<ProductResponseForSelectedSubCategory> getBySubCategoryId(Long subCategoryId);

    ProductResponseForGetAll getBySellerId(Long sellerId);

    CompletableFuture<ProductResponseForSelectedSubCategory> getByComplexFiltering(ProductFilterRequest request);

    Product giveRating(Long id, Double rating);

    Product toggleFavorite(Long id);

    Product update(Long id, ProductRequest request, List<MultipartFile> newImages) throws IOException;

    void delete(Long id);
}
