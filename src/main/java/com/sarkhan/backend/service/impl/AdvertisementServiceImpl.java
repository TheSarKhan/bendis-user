package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.advertisement.AdvertisementRequest;
import com.sarkhan.backend.dto.advertisement.AdvertisementResponse;
import com.sarkhan.backend.dto.advertisement.AdvertisementUpdateRequest;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.mapper.advertisement.AdvertisementMapper;
import com.sarkhan.backend.model.story.Advertisement;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.story.AdvertisementRepository;
import com.sarkhan.backend.service.AdvertisementService;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.impl.product.util.UserUtil;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private static final String FOLDER_NAME = "advertisement";

    private final AdvertisementRepository advertisementRepository;

    private final CloudinaryService cloudinaryService;

    private final UserService userService;

    @Override
    public void save(AdvertisementRequest request, MultipartFile image) throws IOException {
        String url = cloudinaryService.uploadFile(image, FOLDER_NAME).getUrl();
        Advertisement advertisement = AdvertisementMapper.requestTo(request);
        advertisement.setImageUrl(url);

        advertisementRepository.save(advertisement);
    }

    @Override
    public List<AdvertisementResponse> getAll() {
        return advertisementRepository.findAll()
                .stream()
                .map(AdvertisementMapper::toResponse)
                .toList();
    }

    @Override
    public List<AdvertisementResponse> getAllPage(int page) {
        return advertisementRepository.findAll(PageRequest.of(page, 10))
                .stream()
                .map(AdvertisementMapper::toResponse)
                .toList();
    }

    @Override
    public AdvertisementResponse getById(UUID id) {
        Advertisement advertisement = getAdvertisementById(id);
        try {
            User currentUser = UserUtil.getCurrentUser(userService, log);
            Set<Long> view = advertisement.getView();
            view.add(currentUser.getId());
            advertisement.setView(view);
            advertisement = advertisementRepository.save(advertisement);
        } catch (AuthException ignored) {
        }
        return AdvertisementMapper.toResponse(advertisement);
    }

    private Advertisement getAdvertisementById(UUID id) {
        return advertisementRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Advertisement cannot found."));
    }

    @Override
    public void update(AdvertisementUpdateRequest request, MultipartFile image) throws IOException {
        Advertisement advertisement = getAdvertisementById(request.id());
        Advertisement updatedAdvertisement = AdvertisementMapper.updateRequestTo(advertisement, request.request());

        cloudinaryService.deleteFile(advertisement.getImageUrl());
        String url = cloudinaryService.uploadFile(image, FOLDER_NAME).getUrl();
        advertisement.setImageUrl(url);

        advertisementRepository.save(updatedAdvertisement);
    }

    @Override
    public void delete(UUID id) throws IOException {
        Advertisement advertisement = getAdvertisementById(id);
        cloudinaryService.deleteFile(advertisement.getImageUrl());
        advertisementRepository.delete(advertisement);
    }
}
