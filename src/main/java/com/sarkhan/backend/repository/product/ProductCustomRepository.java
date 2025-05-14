package com.sarkhan.backend.repository.product;

import com.sarkhan.backend.dto.product.ProductFilterRequest;
import com.sarkhan.backend.model.product.Product;

import java.util.List;

public interface ProductCustomRepository {
    List<Product> getByComplexFiltering(ProductFilterRequest request);

}
