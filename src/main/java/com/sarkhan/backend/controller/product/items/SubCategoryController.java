package com.sarkhan.backend.controller.product.items;

import com.sarkhan.backend.dto.product.items.SubCategoryRequest;
import com.sarkhan.backend.model.product.items.SubCategory;
import com.sarkhan.backend.service.product.items.SubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/sub-category")
@RequiredArgsConstructor
@Tag(name = "SubCategory Controller", description = "Endpoints for managing sub-categories")
public class SubCategoryController {
    private final SubCategoryService service;

    @PostMapping
    @Operation(
            summary = "Add a new sub-category",
            description = "Creates a new sub-category based on the given request object. Only ADMINs can perform this operation."
    )
    public ResponseEntity<SubCategory> add(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody SubCategoryRequest request) {
        return ResponseEntity.ok(service.add(request));
    }

    @GetMapping
    @Operation(
            summary = "Get all sub-categories",
            description = "Returns a list of all sub-categories in the system."
    )
    public ResponseEntity<List<SubCategory>> getAll(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get sub-category by ID",
            description = "Returns the sub-category that matches the given ID."
    )
    public ResponseEntity<SubCategory> getById(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(
            summary = "Get sub-category by name",
            description = "Returns the sub-category that matches the given name."
    )
    public ResponseEntity<SubCategory> getByName(@RequestHeader("Authorization") String authHeader,
                                                 @PathVariable String name) {
        return ResponseEntity.ok(service.getByName(name));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "Get sub-categories by category ID",
            description = "Returns a list of sub-categories belonging to a specific category."
    )
    public ResponseEntity<List<SubCategory>> getByCategoryId(@RequestHeader("Authorization") String authHeader,
                                                             @PathVariable Long categoryId) {
        return ResponseEntity.ok(service.getByCategoryId(categoryId));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a sub-category",
            description = "Updates a sub-category by ID using the provided request data. Only ADMINs can perform this action."
    )
    public ResponseEntity<SubCategory> update(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable Long id,
                                              @RequestBody SubCategoryRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a sub-category",
            description = "Deletes the sub-category with the specified ID. Only ADMINs can delete sub-categories."
    )
    public ResponseEntity<Void> delete(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}