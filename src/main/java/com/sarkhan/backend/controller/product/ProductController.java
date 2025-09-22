package com.sarkhan.backend.controller.product;

import com.sarkhan.backend.dto.product.*;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Endpoints for managing products")
public class ProductController {
    private final ProductService service;


    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Retrieves a list of all products in the system."
    )
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/famous")
    @Operation(
            summary = "Get all famous products",
            description = "Fetches all products marked as famous. Typically includes trending or highly recognized items."
    )
    public ResponseEntity<List<ProductResponseForGroupOfProduct>> getAllFamousProducts() {
        return ResponseEntity.ok(service.getAllFamousProducts());
    }

    @GetMapping("/discounted")
    @Operation(
            summary = "Get all discounted products",
            description = "Fetches all products that currently have active discounts."
    )
    public ResponseEntity<List<ProductResponseForGroupOfProduct>> getAllDiscountedProducts() {
        return ResponseEntity.ok(service.getAllDiscountedProducts());
    }

    @GetMapping("/most-favorite")
    @Operation(
            summary = "Get all most favorite products",
            description = "Fetches products that are mostly add favorite by users."
    )
    public ResponseEntity<List<ProductResponseForGroupOfProduct>> getAllMostFavoriteProducts() {
        return ResponseEntity.ok(service.getAllMostFavoriteProducts());
    }

    @GetMapping("/flush")
    @Operation(
            summary = "Get all flush products",
            description = "Fetches all products that has limited-stock."
    )
    public ResponseEntity<List<ProductResponseForGroupOfProduct>> getAllFlushProducts() {
        return ResponseEntity.ok(service.getAllFlushProducts());
    }

    @GetMapping("/recommended")
    @Operation(
            summary = "Get all recommended products",
            description = "Fetches products recommended to the user based on preferences or system logic."
    )
    public ResponseEntity<List<ProductResponseForGroupOfProduct>> getAllRecommendedProduct() {
        return ResponseEntity.ok(service.getAllRecommendedProduct());
    }


    @GetMapping("/id")
    @Operation(
            summary = "Get product by ID",
            description = "Fetches a single product by its unique identifier (ID)."
    )
    public ResponseEntity<ProductResponseForGetSingleOne> getById(@RequestParam Long id) {
        return ResponseEntity.ok(service.getByIdAndAddHistory(id));
    }

    @GetMapping("/slug")
    @Operation(
            summary = "Get product by slug",
            description = "Retrieves a product using its unique slug value."
    )
    public ResponseEntity<ProductResponseForGetSingleOne> getBySlug(@RequestParam String slug) {
        return ResponseEntity.ok(service.getBySlug(slug));
    }

    @GetMapping("/name")
    @Operation(
            summary = "Search products by name with fuzzy search",
            description = "Returns products whose names approximately match the provided name (supports typo tolerance)"
    )
    public ResponseEntity<ProductResponseForSearchByName> search(@RequestParam String name) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.searchByName(name).get());
    }

    @GetMapping("/sub-category")
    @Operation(
            summary = "Get products by sub-category",
            description = "Returns all products that belong to a given sub-category ID."
    )
    public ResponseEntity<ProductResponseForSelectedSubCategoryAndComplexFilter> getBySubCategoryId(@RequestParam Long subCategoryId) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.getBySubCategoryId(subCategoryId));
    }

    @GetMapping("/seller")
    @Operation(
            summary = "Get products by seller",
            description = "Retrieves all products associated with a specific seller ID."
    )
    public ResponseEntity<List<ProductResponseForGroupOfProduct>> getBySellerId(@RequestParam Long sellerId) {
        return ResponseEntity.ok(service.getBySellerId(sellerId));
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Filter products with complex criteria",
            description = "Applies multiple filtering criteria (e.g., price range, category, rating) to retrieve matching products."
    )
    public ResponseEntity<ProductResponseForSelectedSubCategoryAndComplexFilter> getByComplexFilter(@RequestBody ProductFilterRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.getByComplexFiltering(request).get());
    }

    @GetMapping("/favorite")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get favorite products",
            description = "Retrieves the list of all products marked as favorite by the currently authenticated user."
    )
    public ResponseEntity<List<Product>> getAllFavorite() throws AuthException {
        return ResponseEntity.ok(service.getAllFavorite());
    }

    @PatchMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Toggle product favorite status",
            description = "Marks or unMarks the given product as a favorite for the current user."
    )
    public ResponseEntity<ProductResponseForGetSingleOne> toggleFavorite(@RequestParam Long id) throws AuthException {
        return ResponseEntity.ok(service.toggleFavorite(id));
    }
}
