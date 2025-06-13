package com.sarkhan.backend.service.impl.product.util;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import com.sarkhan.backend.service.CloudinaryService;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductImageUtil {

    public static List<ColorAndSize> uploadImages(ProductRequest request, List<MultipartFile> images, CloudinaryService cloudinaryService, Logger log) throws IOException {
        List<CloudinaryUploadResponse> colorPhotos = cloudinaryService.uploadFiles(images, "color");

        List<ColorAndSize> colorAndSizes = new ArrayList<>();
        int photoIndex = 0;

        for (ColorAndSize colorAndSize : request.colorAndSizes()) {
            int count = colorAndSize.getPhotoCount();

            if (photoIndex + count > colorPhotos.size()) {
                log.warn("There isn't enough photo: " + colorAndSize.getColorAndSize() + " need " + count + " photo. There are " + (colorPhotos.size() - photoIndex) + " photos.");
                break;
            }

            List<String> photoUrls = colorPhotos.subList(photoIndex, photoIndex + count).stream().map(CloudinaryUploadResponse::getUrl).toList();

            colorAndSize.setImages(photoUrls);
            colorAndSizes.add(colorAndSize);
            log.info("Color added : " + colorAndSize.getColorAndSize());
            photoIndex += count;
        }
        return colorAndSizes;
    }

    public static void deleteAllImages(Product product, CloudinaryService cloudinaryService) throws IOException {
        if (product.getColorAndSizes() != null) {
            for (ColorAndSize colorAndSize : product.getColorAndSizes()) {
                if (colorAndSize.getImages() != null) {
                    for (String imageUrl : colorAndSize.getImages()) {
                        cloudinaryService.deleteFile(imageUrl);
                    }
                }
            }
        }
    }
}
