package com.sarkhan.backend.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarkhan.backend.dto.product.ProductFilterRequest;
import com.sarkhan.backend.dto.product.ProductMapper;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.dto.product.ProductResponse;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Yeni məhsul əlavə et (Admin və Seller üçün)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ProductResponse> addProduct(
            @RequestPart("productRequest") String productRequestJson,
            @RequestPart("images") List<MultipartFile> images) throws IOException {

        // JSON-u parse etmək
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequest productRequest = objectMapper.readValue(productRequestJson, ProductRequest.class);

        Product product = productService.add(productRequest, images);
        ProductResponse response = ProductMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }

    // Bütün məhsulları gətir
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.getAll();
        List<ProductResponse> responses = products.stream()
                .map(ProductMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // İd-yə görə məhsul gətir
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productService.getById(id);
        ProductResponse response = ProductMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }

    // Sluga görə məhsul gətir
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        Product product = productService.getBySlug(slug);
        ProductResponse response = ProductMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }

    // Ad üzrə axtarış
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.searchByName(name);
        List<ProductResponse> responses = products.stream()
                .map(ProductMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // SubCategory id-yə görə məhsullar
    @GetMapping("/subcategory/{subCategoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsBySubCategoryId(@PathVariable Long subCategoryId) {
        List<Product> products = productService.getBySubCategoryId(subCategoryId);
        List<ProductResponse> responses = products.stream()
                .map(ProductMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // Seller id-yə görə məhsullar
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ProductResponse>> getProductsBySellerId(@PathVariable Long sellerId) {
        List<Product> products = productService.getBySellerId(sellerId);
        List<ProductResponse> responses = products.stream()
                .map(ProductMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // Kompleks filtrləmə (POST ilə JSON obyekti göndərilərək)
    @PostMapping("/filter")
    public ResponseEntity<List<ProductResponse>> filterProducts(@RequestBody ProductFilterRequest filterRequest) {
        List<Product> products = productService.getByComplexFiltering(filterRequest);
        List<ProductResponse> responses = products.stream()
                .map(ProductMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // Məhsula reytinq vermək
    @PostMapping("/{productId}/rating")
    public ResponseEntity<ProductResponse> rateProduct(@PathVariable Long productId,
                                                       @RequestParam Double rating) {
        Product product = productService.giveRating(productId, rating);
        ProductResponse response = ProductMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }

    // Məhsulu favoritə əlavə/sil
    @PostMapping("/{productId}/favorite")
    public ResponseEntity<ProductResponse> toggleFavorite(@PathVariable Long productId) {
        Product product = productService.toggleFavorite(productId);
        ProductResponse response = ProductMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }

    // Məhsulun məlumatını yenilə
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @RequestPart ProductRequest productRequest,
                                                         @RequestPart List<MultipartFile> newImages) throws IOException {
        Product product = productService.update(id, productRequest, newImages);
        ProductResponse response = ProductMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }

    // Məhsulu sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

