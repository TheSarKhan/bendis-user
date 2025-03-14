package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.ProductService;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    public Product addProduct(@RequestPart ProductRequest productRequest, List<MultipartFile> images) throws IOException {
Product product = productService.addProduct(productRequest,images);
        return productRepository.save(product);
    }
}
