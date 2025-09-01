package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.advertisement.AdvertisementRequest;
import com.sarkhan.backend.dto.advertisement.AdvertisementResponse;
import com.sarkhan.backend.dto.advertisement.AdvertisementUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AdvertisementService {
    void save(AdvertisementRequest request, MultipartFile image) throws IOException;

    List<AdvertisementResponse> getAll();

    List<AdvertisementResponse> getAllPage(int page);

    AdvertisementResponse getById(UUID id);

    void update(AdvertisementUpdateRequest request, MultipartFile image) throws IOException;

    void delete(UUID id) throws IOException;
}
