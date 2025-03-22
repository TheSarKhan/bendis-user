package com.sarkhan.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folder) throws IOException;
    List<String> uploadFiles(List<MultipartFile> files, String folder) throws IOException;

}
