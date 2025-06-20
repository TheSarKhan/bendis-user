package com.sarkhan.backend.dto.home;

import com.sarkhan.backend.dto.product.ProductResponseForHomePage;
import com.sarkhan.backend.model.story.Story;

import java.util.List;

public record HomePageResponseDTO(ProductResponseForHomePage productResponse,
                                  List<Story> stories) {
}
