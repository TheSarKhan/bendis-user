package com.sarkhan.backend.service.product;

import com.sarkhan.backend.dto.product.ProductFilterRequest;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.model.product.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    Product add(ProductRequest request, List<MultipartFile> images) throws IOException;

    List<Product> getAll();

    Product getById(Long id);

    Product getBySlug(String slug);

    List<Product> searchByName(String name);

    List<Product> getBySubCategoryId(Long subCategoryId);

    List<Product> getBySellerId(Long sellerId);

    List<Product> getByComplexFiltering(ProductFilterRequest request);

    Product giveRating(Long productId, Double rating);

    Product toggleFavorite(Long productId);

    Product update(Long id, ProductRequest request, List<MultipartFile> newImages) throws IOException;

    void delete(Long id);
}
