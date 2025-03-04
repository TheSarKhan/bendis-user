package com.sarkhan.backend.service.impl;



 import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", folder)
        );

        return uploadResult.get("url").toString();
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files, String folder) throws IOException {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urls.add(uploadFile(file, folder));
            }
        }

        return urls;
    }
}
