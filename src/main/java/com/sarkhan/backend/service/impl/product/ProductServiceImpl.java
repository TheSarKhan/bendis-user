package com.sarkhan.backend.service.impl.product;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.dto.product.ProductFilterRequest;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.mapper.ProductMapper;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Color;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    @Override
    public Product add(ProductRequest request, List<MultipartFile> images) throws IOException {
        User user = getCurrentUser();

        log.info(user.getNameAndSurname() + " try to create product");

        Product product = ProductMapper.toEntity(request, user);

        List<Color> colors = uploadImages(request, images);

        product.setColors(colors);

        log.info("Product create successfully.");
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAll() {
        log.info("Someone try to get all products.");
        return productRepository.findAll();
    }

    @Override
    public Product getById(Long id) {
        log.info("Someone try to get a product. Id : " + id);
        return productRepository.findById(id).orElseThrow(() -> {
            log.info("Cannot find product by " + id + " id.");
            return new NoSuchElementException("Cannot find product by " + id + " id.");
        });
    }

    @Override
    public Product getBySlug(String slug) {
        log.info("Someone try to get a product. Slug : " + slug);
        return productRepository.getBySlug(slug).orElseThrow(() -> {
            log.info("Cannot find product by " + slug + " slug.");
            return new NoSuchElementException("Cannot find product by " + slug + " slug.");
        });
    }

    @Override
    public List<Product> searchByName(String name) {
        log.info("Someone try to get products. Name : " + name);
        return productRepository.searchByName(name);
    }

    @Override
    public List<Product> getBySubCategoryId(Long subCategoryId) {
        log.info("Someone try to get products. SubCategory id : " + subCategoryId);
        return productRepository.getBySubCategoryId(subCategoryId);
    }

    @Override
    public List<Product> getBySellerId(Long sellerId) {
        log.info("Someone try to get products. Seller id : " + sellerId);
        return productRepository.getBySellerId(sellerId);
    }

    @Override
    public List<Product> getByComplexFiltering(ProductFilterRequest request) {
        log.info("Someone try to get product with complex params.");
        return productRepository.getByComplexFiltering(request);
    }

    @Override
    public Product giveRating(Long productId, Double rating) {
        User user = getCurrentUser();
        Product product = getById(productId);

        log.info(user.getNameAndSurname() + " try to give rating. Product name : " + product.getName());

        Map<Long, Double> ratings = product.getRatings();
        if (ratings.containsKey(user.getId())) {
            log.warn(user.getNameAndSurname() + " try to give additional rating.");
            return product;
        }
        Double oldRating = product.getRating();
        Double newRating = (oldRating * ratings.size() + rating) / (ratings.size() + 1);
        ratings.put(user.getId(), rating);

        product.setRating(newRating);
        product.setRatings(ratings);
        return productRepository.save(product);
    }

    @Override
    public Product toggleFavorite(Long productId) {
        User user = getCurrentUser();
        Product product = getById(productId);

        log.info(user.getNameAndSurname() + " pres favorite button. Product name : " + product.getName());

        Set<Long> favorites = product.getFavorites();
        if (favorites.contains(user.getId())) {
            log.info("User remove favorite.");
            favorites.remove(user.getId());
        } else {
            log.info("User add favorite.");
            favorites.add(user.getId());
        }
        product.setFavorites(favorites);

        return productRepository.save(product);
    }

    @Override
    public Product update(Long id, ProductRequest request, List<MultipartFile> newImages) throws IOException {
        Product oldProduct = ProductMapper.updateEntity(getById(id), request);
        User user = getCurrentUser();

        if (!oldProduct.getSellerId().equals(user.getId()) || Role.ADMIN.equals(user.getRole())) {
            throw new AccessDeniedException("You are not authorized to update this product");
        }

        Product product = ProductMapper.updateEntity(oldProduct, request);

        deleteAllImages(product);

        List<Color> colors = uploadImages(request, newImages);

        product.setColors(colors);

        log.info("Product update successfully.");
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        User user = getCurrentUser();
        log.warn(user.getNameAndSurname() + " delete product. Id : " + id);
        productRepository.deleteById(id);
    }

    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("Cannot find user by email: " + email);
            return new NoSuchElementException("Cannot find user by email: " + email);
        });
    }

    private List<Color> uploadImages(ProductRequest request, List<MultipartFile> images) throws IOException {
        List<CloudinaryUploadResponse> colorPhotos = cloudinaryService.uploadFiles(images, "color");

        List<Color> colors = new ArrayList<>();
        int photoIndex = 0;

        for (Color color : request.colors()) {
            int count = color.getPhotoCount();

            if (photoIndex + count > colorPhotos.size()) {
                log.warn("There isn't enough photo: " + color.getColor() +
                         " need " + count +
                         " photo. There are " + (colorPhotos.size() - photoIndex) +
                         " photos.");
                break;
            }

            List<String> photoUrls = colorPhotos.subList(photoIndex, photoIndex + count).stream()
                    .map(CloudinaryUploadResponse::getUrl)
                    .toList();

            color.setImages(photoUrls);
            colors.add(color);

            log.info("Color added : " + color.getColor());

            photoIndex += count;
        }
        return colors;
    }

    private void deleteAllImages(Product product) throws IOException {
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