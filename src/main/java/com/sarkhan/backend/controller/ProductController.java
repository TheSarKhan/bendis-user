package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductService productService;

    @GetMapping
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product addProduct(@RequestPart ProductRequest productRequest, List<MultipartFile> images,@RequestHeader("Authorization") String token) throws IOException {
        token=token.substring(7);
        Product product = productService.addProduct(productRequest,images,token);
        return productRepository.save(product);
    }
    @GetMapping("/{brand}/{slug}-p-{productId}")
    public ResponseEntity<Product> getProductByCustomUrl(@PathVariable String brand,
                                                         @PathVariable String slug,
                                                         @PathVariable Long productId) {
        Product product = productService.getProductByDetails(brand, slug, productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}
