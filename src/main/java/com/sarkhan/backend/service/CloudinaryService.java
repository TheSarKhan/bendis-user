package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudinaryService {
    CloudinaryUploadResponse uploadFile(MultipartFile file, String folder)  throws IOException;
    List<CloudinaryUploadResponse> uploadFiles(List<MultipartFile> files, String folder)  throws IOException;
    String deleteImage(String publicId) throws IOException;
    String updateImage(String publicId, MultipartFile newFile) throws IOException;
    List<CloudinaryUploadResponse> updateImages(List<String> publicIds, List<MultipartFile> newFiles) throws IOException;
    List<String> deleteImages(List<String> publicIds) throws IOException;
    void deleteFile(String imageUrl) throws IOException;
}
