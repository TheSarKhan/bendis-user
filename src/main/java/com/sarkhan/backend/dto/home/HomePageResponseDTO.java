package com.sarkhan.backend.dto.home;

import com.sarkhan.backend.dto.advertisement.AdvertisementResponse;
import com.sarkhan.backend.dto.product.ProductResponseForHomePage;
import com.sarkhan.backend.model.story.Story;

import java.util.List;

public record HomePageResponseDTO(ProductResponseForHomePage productResponse,
                                  List<AdvertisementResponse> advertisements,
                                  List<Story> stories) {
}
