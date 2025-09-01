package com.sarkhan.backend.dto.advertisement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AdvertisementRequest(
    @NotBlank
    @Schema(example = "My Advertisement")
    String title,
    @NotBlank
    @Schema(example = "https://www.google.com")
    String link,
    @Schema(example = "This is my advertisement")
    String description
) {
}
