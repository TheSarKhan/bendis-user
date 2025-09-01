package com.sarkhan.backend.mapper.advertisement;

import com.sarkhan.backend.dto.advertisement.AdvertisementRequest;
import com.sarkhan.backend.dto.advertisement.AdvertisementResponse;
import com.sarkhan.backend.model.story.Advertisement;

public class AdvertisementMapper {
    public static Advertisement requestTo(AdvertisementRequest request) {
        return Advertisement.builder()
                .title(request.title())
                .link(request.link())
                .description(request.description())
                .build();
    }

    public static Advertisement updateRequestTo(Advertisement advertisement, AdvertisementRequest request) {
        advertisement.setTitle(request.title());
        advertisement.setDescription(request.description());
        advertisement.setLink(request.link());
        return advertisement;
    }

    public static AdvertisementResponse toResponse(Advertisement advertisement) {
        return new AdvertisementResponse(
                advertisement.getId(),
                advertisement.getTitle(),
                advertisement.getImageUrl(),
                advertisement.getLink(),
                advertisement.getDescription(),
                advertisement.getView().size(),
                advertisement.getCreatedAt(),
                advertisement.getUpdatedAt());
    }
}
