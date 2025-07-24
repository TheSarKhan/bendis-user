package com.sarkhan.backend.controller.product.items;

import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.service.product.items.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/category")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Category", description = "Endpoints for managing categories")
public class CategoryController {
    private final CategoryService service;

    @PostMapping
    @Operation(
            summary = "Add a new category",
            description = "Creates a new category with the given name."
    )
    public ResponseEntity<Category> add(@RequestBody String name) {
        return ResponseEntity.ok(service.add(name));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @Operation(
            summary = "Get all categories",
            description = "Returns a list of all available categories."
    )
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @Operation(
            summary = "Get category by ID",
            description = "Fetches a category by its unique identifier (ID)."
    )
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(
            summary = "Get category by name",
            description = "Fetches a category by its name value."
    )
    public ResponseEntity<Category> getByName(@PathVariable String name) {
        return ResponseEntity.ok(service.getByName(name));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a category",
            description = "Updates the name of the category with the specified ID."
    )
    public ResponseEntity<Category> update(@PathVariable Long id,
                                           @RequestBody String name) {
        return ResponseEntity.ok(service.update(id, name));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a category",
            description = "Deletes a category by its ID."
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
