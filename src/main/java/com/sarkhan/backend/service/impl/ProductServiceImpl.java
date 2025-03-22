package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Color;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Product addProduct(ProductRequest productRequest, List<MultipartFile> images,String token) throws IOException {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescriptions(productRequest.getDescriptions());
        product.setCategory(productRequest.getCategory());
        product.setSpecifications(productRequest.getSpecifications());
        product.setPluses(productRequest.getPluses());
        List<String> colorPhotos = cloudinaryService.uploadFiles(images, "color");

        List<Color> colors = new ArrayList<>();
        int photoIndex = 0;
        for (int i = 0; i < productRequest.getColors().size(); i++) {
            List<String> photoUrls = new ArrayList<>();
            Color color = new Color();
            color.setColor(productRequest.getColors().get(i).getColor());
            color.setStock(productRequest.getColors().get(i).getStock());
            color.setPhotoCount(productRequest.getColors().get(i).getPhotoCount());
            for (int j = 0; j < productRequest.getColors().get(i).getPhotoCount(); j++) {
          if (photoIndex < colorPhotos.size()) {
            String photo = colorPhotos.get(photoIndex);
            photoUrls.add(photo);
            photoIndex++;
                }else {
              break;
          }
            }
            color.setImages(photoUrls);
            colors.add(color);
            System.out.println("Reng elave edildi: " + color.getColor());
        }
        product.setColors(colors);
        return productRepository.save(product);

    }
}