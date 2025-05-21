package com.sarkhan.backend.controller.product;

import com.sarkhan.backend.dto.product.ProductFilterRequest;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.dto.product.ProductResponseForGetAll;
import com.sarkhan.backend.dto.product.ProductResponseForSelectedSubCategory;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "Endpoints for managing products")
public class ProductController {
    private final ProductService service;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(
            summary = "Create a new product",
            description = "Adds a new product along with its images. Only users with ADMIN or SELLER roles are allowed."
    )
    public ResponseEntity<Product> add(@RequestPart ProductRequest productRequest,
                                       @RequestPart List<MultipartFile> images) throws IOException {
        return ResponseEntity.ok(service.add(productRequest, images));
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Retrieves a list of all products in the system."
    )
    public ResponseEntity<ProductResponseForGetAll> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Fetches a single product by its unique identifier (ID)."
    )
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
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
    public ResponseEntity<ProductResponseForGetAll> search(@PathVariable String name){
        return ResponseEntity.ok(service.searchByName(name));
    }

    @GetMapping("/sub-category/{subCategoryId}")
    @Operation(
            summary = "Get products by sub-category",
            description = "Returns all products that belong to a given sub-category ID."
    )
    public ResponseEntity<ProductResponseForSelectedSubCategory> getBySubCategoryId(@PathVariable Long subCategoryId) {
        return ResponseEntity.ok(service.getBySubCategoryId(subCategoryId));
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(
            summary = "Get products by seller",
            description = "Retrieves all products associated with a specific seller ID."
    )
    public ResponseEntity<ProductResponseForGetAll> getBySellerId(@PathVariable Long sellerId) {
        return ResponseEntity.ok(service.getBySellerId(sellerId));
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Filter products with complex criteria",
            description = "Applies multiple filtering criteria (e.g., price range, category, rating) to retrieve matching products."
    )
    public ResponseEntity<ProductResponseForSelectedSubCategory> getByComplexFilter(@ModelAttribute ProductFilterRequest request) {
        return ResponseEntity.ok(service.getByComplexFiltering(request));
    }

    @PatchMapping("/{id}/rating/{rating}")
    @Operation(
            summary = "Rate a product",
            description = "Allows a user to submit a rating for a product by its ID."
    )
    public ResponseEntity<Product> giveRating(@PathVariable Long id,
                                              @PathVariable Double rating) {
        return ResponseEntity.ok(service.giveRating(id, rating));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Toggle product favorite status",
            description = "Marks or unMarks the given product as a favorite for the current user."
    )
    public ResponseEntity<Product> toggleFavorite(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleFavorite(id));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(
            summary = "Update a product",
            description = "Updates the details and images of an existing product. Only users with ADMIN or SELLER roles are allowed."
    )
    public ResponseEntity<Product> update(@PathVariable Long id,
                                          @RequestPart ProductRequest productRequest,
                                          @RequestPart List<MultipartFile> images) throws IOException {
        return ResponseEntity.ok(service.update(id, productRequest, images));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(
            summary = "Delete a product",
            description = "Deletes a product by its ID. Only users with ADMIN or SELLER roles are allowed to perform this operation."
    )
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
