package com.sarkhan.backend.dto.advertisement;

import java.time.Instant;
import java.util.UUID;

public record AdvertisementResponse(
        UUID id,
        String title,
        String imageUrl,
        String link,
        String description,
        int viewCount,
        Instant createAt,
        Instant updateAt
) {
}
