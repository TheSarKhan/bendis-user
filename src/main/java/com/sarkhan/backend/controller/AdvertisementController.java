package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.advertisement.AdvertisementRequest;
import com.sarkhan.backend.dto.advertisement.AdvertisementResponse;
import com.sarkhan.backend.dto.advertisement.AdvertisementUpdateRequest;
import com.sarkhan.backend.service.AdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/advertisements")
@Tag(name = "Advertisements", description = "APIs for managing advertisements")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create Advertisement",
            description = "Create a new advertisement with details and an image (ADMIN only)"
    )
    public ResponseEntity<String> create(@RequestPart
                                         @Valid
                                         AdvertisementRequest request,
                                         @RequestPart
                                         MultipartFile image) throws IOException {
        advertisementService.save(request, image);
        return ResponseEntity.ok("Advertisement created successfully");
    }

    @GetMapping
    @Operation(
            summary = "Get All Advertisements",
            description = "Retrieve all available advertisements"
    )
    public ResponseEntity<List<AdvertisementResponse>> getAll() {
        return ResponseEntity.ok(advertisementService.getAll());
    }

    @GetMapping("/page")
    @Operation(
            summary = "Get Advertisements by Page",
            description = "Retrieve advertisements in paginated format by page number"
    )
    public ResponseEntity<List<AdvertisementResponse>> getAllByPage(@RequestParam int page) {
        return ResponseEntity.ok(advertisementService.getAllPage(page));
    }

    @GetMapping("/id")
    @Operation(
            summary = "Get Advertisement by ID",
            description = "If someone click advertisement for open link please must be also send request this api." +
                    " Retrieve a specific advertisement by its unique ID."
    )
    public ResponseEntity<AdvertisementResponse> getById(@RequestParam UUID id) {
        return ResponseEntity.ok(advertisementService.getById(id));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update Advertisement",
            description = "Update an existing advertisement with new details and an image (ADMIN only)"
    )
    public ResponseEntity<String> update(@RequestPart
                                         @Valid
                                         AdvertisementUpdateRequest request,
                                         @RequestPart
                                         MultipartFile image) throws IOException {
        advertisementService.update(request, image);
        return ResponseEntity.ok("Advertisement updated successfully");
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete Advertisement",
            description = "Delete an advertisement by its ID (ADMIN only)"
    )
    public ResponseEntity<Void> delete(@RequestParam UUID id) throws IOException {
        advertisementService.delete(id);
        return ResponseEntity.ok().build();
    }
}
