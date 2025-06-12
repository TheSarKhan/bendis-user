package com.sarkhan.backend.service.impl.product.util;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Color;
import com.sarkhan.backend.service.CloudinaryService;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductImageUtil {

    public static List<Color> uploadImages(ProductRequest request, List<MultipartFile> images, CloudinaryService cloudinaryService, Logger log) throws IOException {
        List<CloudinaryUploadResponse> colorPhotos = cloudinaryService.uploadFiles(images, "color");

        List<Color> colors = new ArrayList<>();
        int photoIndex = 0;

        for (Color color : request.colors()) {
            int count = color.getPhotoCount();

            if (photoIndex + count > colorPhotos.size()) {
                log.warn("There isn't enough photo: " + color.getColor() + " need " + count + " photo. There are " + (colorPhotos.size() - photoIndex) + " photos.");
                break;
            }

            List<String> photoUrls = colorPhotos.subList(photoIndex, photoIndex + count).stream().map(CloudinaryUploadResponse::getUrl).toList();

            color.setImages(photoUrls);
            colors.add(color);
            log.info("Color added : " + color.getColor());
            photoIndex += count;
        }
        return colors;
    }

    public static void deleteAllImages(Product product, CloudinaryService cloudinaryService) throws IOException {
        if (product.getColors() != null) {
            for (Color color : product.getColors()) {
                if (color.getImages() != null) {
                    for (String imageUrl : color.getImages()) {
                        cloudinaryService.deleteFile(imageUrl);
                    }
                }
            }
        }
    }
}
