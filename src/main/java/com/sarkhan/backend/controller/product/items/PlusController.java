package com.sarkhan.backend.controller.product.items;

import com.sarkhan.backend.model.product.items.Plus;
import com.sarkhan.backend.service.product.items.PlusService;
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
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/plus")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Plus", description = "Endpoints for managing Plus items (header, description, icon)")
public class PlusController {
    private final PlusService service;

    @PostMapping
    @Operation(
            summary = "Add a new Plus item",
            description = "Creates a new Plus item with a header, description, and icon file. Only ADMINs can perform this action."
    )
    public ResponseEntity<Plus> add(@RequestPart String header,
                                    @RequestPart String description,
                                    MultipartFile icon) throws IOException {
        return ResponseEntity.ok(service.add(header, description, icon));
    }

    @GetMapping
    @Operation(
            summary = "Get all Plus items",
            description = "Returns a list of all Plus items in the system."
    )
    public ResponseEntity<List<Plus>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/id")
    @Operation(
            summary = "Get Plus item by ID",
            description = "Fetches a Plus item using its unique ID."
    )
    public ResponseEntity<Plus> getById(@RequestParam Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/header")
    @Operation(
            summary = "Get Plus item by header",
            description = "Fetches a Plus item using its unique header value."
    )
    public ResponseEntity<Plus> getByHeader(@RequestParam String header) {
        return ResponseEntity.ok(service.getByHeader(header));
    }

    @PutMapping
    @Operation(
            summary = "Update an existing Plus item",
            description = "Updates the header, description, and icon of an existing Plus item by ID. Only ADMINs can perform this action."
    )
    public ResponseEntity<Plus> update(@RequestParam Long id,
                                       @RequestPart String header,
                                       @RequestPart String description,
                                       MultipartFile icon) throws IOException {
        return ResponseEntity.ok(service.update(id, header, description, icon));
    }

    @DeleteMapping
    @Operation(
            summary = "Delete a Plus item",
            description = "Deletes a Plus item by its ID. Only ADMINs are allowed to perform this operation."
    )
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
