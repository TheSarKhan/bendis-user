package com.sarkhan.backend.service.product;

import com.sarkhan.backend.dto.product.ProductFilterRequest;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.dto.product.ProductResponseForGetAll;
import com.sarkhan.backend.dto.product.ProductResponseForSelectedSubCategory;
import com.sarkhan.backend.model.product.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    Product add(ProductRequest request, List<MultipartFile> images) throws IOException;

    ProductResponseForGetAll getAll();

    Product getById(Long id);

    Product getBySlug(String slug);

    ProductResponseForGetAll searchByName(String name);

    ProductResponseForSelectedSubCategory getBySubCategoryId(Long subCategoryId);

    ProductResponseForGetAll getBySellerId(Long sellerId);

    ProductResponseForSelectedSubCategory getByComplexFiltering(ProductFilterRequest request);

    Product giveRating(Long id, Double rating);

    Product toggleFavorite(Long id);

    Product update(Long id, ProductRequest request, List<MultipartFile> newImages) throws IOException;

    void delete(Long id);
}
