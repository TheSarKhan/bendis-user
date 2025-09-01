package com.sarkhan.backend.dto.advertisement;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AdvertisementUpdateRequest(
        @NotNull
        UUID id,
        @NotNull
        AdvertisementRequest request
) {
}
