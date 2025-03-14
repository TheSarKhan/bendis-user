package com.sarkhan.backend.repository.product;

import com.sarkhan.backend.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
