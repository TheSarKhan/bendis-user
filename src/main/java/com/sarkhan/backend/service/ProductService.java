package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.model.product.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Product addProduct(ProductRequest productRequest, List<MultipartFile> images,String token) throws IOException;
    Product getProductByDetails(String brand, String slug, Long productId);
}
