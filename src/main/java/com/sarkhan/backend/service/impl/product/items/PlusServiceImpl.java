package com.sarkhan.backend.service.impl.product.items;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.model.product.items.Plus;
import com.sarkhan.backend.repository.product.items.PlusRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.product.items.PlusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlusServiceImpl implements PlusService {
    private final PlusRepository plusRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Plus add(String header, String description, MultipartFile icon) throws IOException {
        log.info("Someone try to create plus. Header : " + header);
        var response = cloudinaryService.uploadFile(icon, "icon");
        return plusRepository.save(Plus.builder()
                .header(header)
                .description(description)
                .iconUrl(response.getUrl())
                .build());
    }

    @Override
    public List<Plus> getAll() {
        log.info("Someone try to get all pluses.");
        return plusRepository.findAll();
    }

    @Override
    public Plus getById(Long id) {
        log.info("Someone try to get plus. id : " + id);
        return plusRepository.findById(id).orElseThrow(() -> {
            log.error("Cannot find any plus with " + id + " id.");
            return new NoSuchElementException("Cannot find any plus with " + id + " id.");
        });
    }

    @Override
    public Plus getByHeader(String header) {
        log.info("Someone try to get plus. Header : " + header);
        return plusRepository.findByHeader(header).orElseThrow(() -> {
            log.error("Cannot find any plus with " + header + " header.");
            return new NoSuchElementException("Cannot find any plus with " + header + " header.");
        });
    }

    @Override
    public Plus update(Long id, String header, String description, MultipartFile icon) throws IOException {
        Plus plus = getById(id);

        log.info("Someone try to update plus. Plus : " + plus);

        plus.setHeader(header);
        plus.setDescription(description);

        cloudinaryService.deleteFile(plus.getIconUrl());

        CloudinaryUploadResponse cloudinaryUploadResponse = cloudinaryService.uploadFile(icon, "icon");

        plus.setIconUrl(cloudinaryUploadResponse.getUrl());

        log.info("Update successfully. new Plus : " + plus);
        return plusRepository.save(plus);
    }

    @Override
    public void delete(Long id) {
        log.warn("Someone try to delete plus. id : " + id);
        plusRepository.deleteById(id);
    }
}
