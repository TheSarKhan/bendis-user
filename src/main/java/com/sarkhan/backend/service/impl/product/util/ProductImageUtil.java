package com.sarkhan.backend.service.impl.product.util;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.dto.product.items.ColorAndSizeRequest;
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

        for (ColorAndSizeRequest colorAndSize : request.colorAndSizeRequests()) {
            int count = colorAndSize.photoCount();

            if (photoIndex + count > colorPhotos.size()) {
                log.warn("There isn't enough photo: " + colorAndSize.color().name() + " need " + count + " photo. There are " + (colorPhotos.size() - photoIndex) + " photos.");
                break;
            }

            List<String> photoUrls = colorPhotos.subList(photoIndex, photoIndex + count).stream().map(CloudinaryUploadResponse::getUrl).toList();

            colorAndSizes.add(ColorAndSize.builder().
                    color(colorAndSize.color()).
                    photoCount(colorAndSize.photoCount()).
                    stock(colorAndSize.stock()).
                    imageUrls(photoUrls).
                    sizeStockMap(colorAndSize.sizeStockMap()).
                    build());
            log.info("Color added : " + colorAndSize.color().name());
            photoIndex += count;
        }
        return colorAndSizes;
    }

    public static void deleteAllImages(Product product, CloudinaryService cloudinaryService) throws IOException {
        if (product.getColorAndSizes() != null) {
            for (ColorAndSize colorAndSize : product.getColorAndSizes()) {
                if (colorAndSize.getImageUrls() != null) {
                    for (String imageUrl : colorAndSize.getImageUrls()) {
                        cloudinaryService.deleteFile(imageUrl);
                    }
                }
            }
        }
    }
}
