package com.sarkhan.backend.controller.product;

import com.sarkhan.backend.dto.product.*;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "Endpoints for managing products")
public class ProductController {
    private final ProductService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(
            summary = "Create a new product",
            description = "Adds a new product along with its images. Only users with ADMIN or SELLER roles are allowed."
    )
    public ResponseEntity<Product> add(@RequestHeader("Authorization") String authHeader,
                                       @RequestPart ProductRequest productRequest,
                                       List<MultipartFile> images) throws IOException, ExecutionException, InterruptedException, AuthException {
        return ResponseEntity.ok(service.add(productRequest, images).get());
    }

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
    public ResponseEntity<ProductResponseSimple> getAllFamousProducts() {
        return ResponseEntity.ok(service.getAllFamousProducts());
    }

    @GetMapping("/discounted")
    @Operation(
            summary = "Get all discounted products",
            description = "Fetches all products that currently have active discounts."
    )
    public ResponseEntity<ProductResponseSimple> getAllDiscountedProducts() {
        return ResponseEntity.ok(service.getAllDiscountedProducts());
    }

    @GetMapping("/most-favorite")
    @Operation(
            summary = "Get all most favorite products",
            description = "Fetches products that are mostly add favorite by users."
    )
    public ResponseEntity<ProductResponseSimple> getAllMostFavoriteProducts() {
        return ResponseEntity.ok(service.getAllMostFavoriteProducts());
    }

    @GetMapping("/flush")
    @Operation(
            summary = "Get all flush products",
            description = "Fetches all products that has limited-stock."
    )
    public ResponseEntity<ProductResponseSimple> getAllFlushProducts() {
        return ResponseEntity.ok(service.getAllFlushProducts());
    }

    @GetMapping("/recommended")
    @Operation(
            summary = "Get all recommended products",
            description = "Fetches products recommended to the user based on preferences or system logic."
    )
    public ResponseEntity<ProductResponseSimple> getAllRecommendedProduct() {
        return ResponseEntity.ok(service.getAllRecommendedProduct());
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Fetches a single product by its unique identifier (ID)."
    )
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByIdAndAddHistory(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(
            summary = "Get product by slug",
            description = "Retrieves a product using its unique slug value."
    )
    public ResponseEntity<Product> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(service.getBySlug(slug));
    }

    @GetMapping("/name/{name}")
    @Operation(
            summary = "Search products by name with fuzzy search",
            description = "Returns products whose names approximately match the provided name (supports typo tolerance)"
    )
    public ResponseEntity<ProductResponseForSearchByName> search(@PathVariable String name) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.searchByName(name).get());
    }

    @GetMapping("/sub-category/{subCategoryId}")
    @Operation(
            summary = "Get products by sub-category",
            description = "Returns all products that belong to a given sub-category ID."
    )
    public ResponseEntity<ProductResponseForSelectedSubCategoryAndComplexFilter> getBySubCategoryId(@PathVariable Long subCategoryId) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.getBySubCategoryId(subCategoryId).get());
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(
            summary = "Get products by seller",
            description = "Retrieves all products associated with a specific seller ID."
    )
    public ResponseEntity<ProductResponseForGetBySellerId> getBySellerId(@PathVariable Long sellerId) {
        return ResponseEntity.ok(service.getBySellerId(sellerId));
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Filter products with complex criteria",
            description = "Applies multiple filtering criteria (e.g., price range, category, rating) to retrieve matching products."
    )
    public ResponseEntity<ProductResponseForSelectedSubCategoryAndComplexFilter> getByComplexFilter(@ModelAttribute ProductFilterRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.getByComplexFiltering(request).get());
    }

    @GetMapping("/favorite")
    @Operation(
            summary = "Get favorite products",
            description = "Retrieves the list of all products marked as favorite by the currently authenticated user."
    )
    public ResponseEntity<List<Product>> getAllFavorite(@RequestHeader("Authorization") String authHeader) throws AuthException {
        return ResponseEntity.ok(service.getAllFavorite());
    }

    @PatchMapping("/{id}/rating/{rating}")
    @Operation(
            summary = "Rate a product",
            description = "Allows a user to submit a rating for a product by its ID."
    )
    public ResponseEntity<Product> giveRating(@PathVariable Long id,
                                              @PathVariable Double rating) throws AuthException {
        return ResponseEntity.ok(service.giveRating(id, rating));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Toggle product favorite status",
            description = "Marks or unMarks the given product as a favorite for the current user."
    )
    public ResponseEntity<Product> toggleFavorite(@RequestHeader("Authorization") String authHeader,
                                                  @PathVariable Long id) throws AuthException {
        return ResponseEntity.ok(service.toggleFavorite(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(
            summary = "Update a product",
            description = "Updates the details and images of an existing product. Only users with ADMIN or SELLER roles are allowed."
    )
    public ResponseEntity<Product> update(@RequestHeader("Authorization") String authHeader,
                                          @PathVariable Long id,
                                          @RequestPart ProductRequest productRequest,
                                          List<MultipartFile> images) throws IOException, AuthException {
        return ResponseEntity.ok(service.update(id, productRequest, images));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(
            summary = "Delete a product",
            description = "Deletes a product by its ID. Only users with ADMIN or SELLER roles are allowed to perform this operation."
    )
    public ResponseEntity<Void> delete(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable Long id) throws AuthException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
