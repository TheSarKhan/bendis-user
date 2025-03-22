package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
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
         Product product = productService.addProduct(productRequest,images,token);
        return productRepository.save(product);
    }
    @GetMapping("/{seller}/{productName}")
    public Product getProduct(@RequestHeader("Authorization") String token,@PathVariable String seller,@PathVariable String productName,@RequestParam String color) throws IOException {
        Optional<Product>product=productRepository.findById(4L);
        return product.get();
    }
}
