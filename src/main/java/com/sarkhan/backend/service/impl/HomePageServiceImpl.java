package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.home.HomePageResponseDTO;
import com.sarkhan.backend.service.AdvertisementService;
import com.sarkhan.backend.service.HomePageService;
import com.sarkhan.backend.service.product.ProductService;
import com.sarkhan.backend.service.story.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomePageServiceImpl implements HomePageService {
    private final ProductService productService;

    private final StoryService storyService;

    private final AdvertisementService advertisementService;

    @Override
    public HomePageResponseDTO getHomePageInfo() {
        return new HomePageResponseDTO(
                productService.getForHomePage(),
                advertisementService.getAll(),
                storyService.getForHomePage());
    }
}
