package com.sarkhan.backend.service.impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;


    @Override
    public CloudinaryUploadResponse uploadFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", folder)
        );

        String url = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        return new CloudinaryUploadResponse(url, publicId);
    }

    @Override
    public List<CloudinaryUploadResponse> uploadFiles(List<MultipartFile> files, String folder) throws IOException {
        List<CloudinaryUploadResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                responses.add(uploadFile(file, folder));
            }
        }

        return responses;
    }

    @Override
    public String deleteImage(String publicId) throws IOException {
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return result.get("result").toString(); // "ok", "not found", "error" gibi döner
    }

    @Override
    public String updateImage(String publicId, MultipartFile newFile) throws IOException {
        // Eski resmi sil
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        // Yeniden yükle aynı ID ile
        Map uploadResult = cloudinary.uploader().upload(newFile.getBytes(), ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true
        ));
        return uploadResult.get("secure_url").toString();
    }
    @Override
    public List<String> deleteImages(List<String> publicIds) throws IOException {
        List<String> results = new ArrayList<>();

        for (String publicId : publicIds) {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            results.add("[" + publicId + "] → " + result.get("result").toString());
        }

        return results;
    }
    @Override
    public List<CloudinaryUploadResponse> updateImages(List<String> publicIds, List<MultipartFile> newFiles) throws IOException {
        if (publicIds.size() != newFiles.size()) {
            throw new IllegalArgumentException("publicIds ve dosya listesi aynı boyutta olmalı");
        }

        List<CloudinaryUploadResponse> updatedImages = new ArrayList<>();

        for (int i = 0; i < publicIds.size(); i++) {
            String publicId = publicIds.get(i);
            MultipartFile newFile = newFiles.get(i);

            // Sil → Upload (aynı ID ile overwrite)
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            Map uploadResult = cloudinary.uploader().upload(newFile.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", true
            ));

            String url = uploadResult.get("secure_url").toString();
            updatedImages.add(new CloudinaryUploadResponse(url, publicId));
        }

        return updatedImages;
    }

}
