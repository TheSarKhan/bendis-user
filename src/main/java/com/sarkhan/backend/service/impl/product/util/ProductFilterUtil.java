package com.sarkhan.backend.service.impl.product.util;

import com.sarkhan.backend.dto.product.ProductFilterRequest;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.specification.ProductSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductFilterUtil {

    public static List<Product> getByComplexFilteringUseSpecification(ProductFilterRequest request, ProductRepository productRepository) {
        Specification<Product> spec = Specification.where(null);

        if (request.subCategoryId() != null) {
            spec = spec.and(ProductSpecification.hasSubCategoryId(request.subCategoryId()));
        }

        if (request.specifications() != null && !request.specifications().isEmpty()) {
            spec = spec.and(ProductSpecification.hasSpecifications(request.specifications()));
        }

        if (request.gender() != null) {
            spec = spec.and(ProductSpecification.hasGender(request.gender()));
        }

        if (request.rating() != null) {
            spec = spec.and(ProductSpecification.graterThanRating(request.rating()));
        }

        if (request.minPrice() != null || request.maxPrice() != null) {
            spec = spec.and(ProductSpecification.betweenPrice(
                    request.minPrice() == null ? 0 : request.minPrice(),
                    request.maxPrice() == null ? Double.MAX_VALUE : request.maxPrice()
            ));
        }

        return productRepository.findAll(spec);
    }
}
