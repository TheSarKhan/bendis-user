package com.sarkhan.backend.service.impl.product;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.dto.product.ProductFilterRequest;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.dto.product.ProductResponseForGetAll;
import com.sarkhan.backend.dto.product.ProductResponseForSelectedSubCategory;
import com.sarkhan.backend.mapper.ProductMapper;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.model.product.items.Color;
import com.sarkhan.backend.model.product.items.SubCategory;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.product.ProductService;
import com.sarkhan.backend.service.product.items.CategoryService;
import com.sarkhan.backend.service.product.items.SubCategoryService;
import com.sarkhan.backend.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
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

    private final CategoryService categoryService;

    private final SubCategoryService subCategoryService;

    private final UserService userService;

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
    public ProductResponseForGetAll getAll() {
        log.info("Someone try to get all products.");
        return new ProductResponseForGetAll(
                productRepository.findAll(),
                categoryService.getAll(),
                subCategoryService.getAll());
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
    public ProductResponseForGetAll searchByName(String name) {
        log.info("Someone try to get products. Name : " + name);
        List<Product> products = productRepository.findAll(ProductSpecification.searchTitle(name));
        List<Category> categories = categoryService.searchByName(name);
        Set<SubCategory> subCategories = new HashSet<>();

        for (Category category : categories)
            subCategories.addAll(subCategoryService.getByCategoryId(category.getId()));

        subCategories.addAll(subCategoryService.searchByName(name));

        for (SubCategory subCategory : subCategories)
            products.addAll(productRepository.getBySubCategoryId(subCategory.getId()));

        return new ProductResponseForGetAll(
                products,
                categoryService.getAll(),
                subCategoryService.getAll());
    }

    @Override
    public ProductResponseForSelectedSubCategory getBySubCategoryId(Long subCategoryId) {
        log.info("Someone try to get products. SubCategory id : " + subCategoryId);
        return new ProductResponseForSelectedSubCategory(
                productRepository.getBySubCategoryId(subCategoryId),
                categoryService.getAll(),
                subCategoryService.getAll(),
                subCategoryService.getById(subCategoryId).getSpecifications(),
                new ProductFilterRequest(subCategoryId, null, null, null, null, null));
    }

    @Override
    public ProductResponseForGetAll getBySellerId(Long sellerId) {
        log.info("Someone try to get products. Seller id : " + sellerId);
        return new ProductResponseForGetAll(
                productRepository.getBySellerId(sellerId),
                categoryService.getAll(),
                subCategoryService.getAll());
    }

    @Override
    public ProductResponseForSelectedSubCategory getByComplexFiltering(ProductFilterRequest request) {//change
        log.info("Someone try to get product with complex params.");
        return new ProductResponseForSelectedSubCategory(
                getByComplexFilteringUseSpecification(request),
                categoryService.getAll(),
                subCategoryService.getAll(),
                subCategoryService.getById(request.subCategoryId()).getSpecifications(),
                request);
    }

    @Override
    public Product giveRating(Long id, Double rating) {
        User user = getCurrentUser();
        Product product = getById(id);

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
    public Product toggleFavorite(Long id) {
        User user = getCurrentUser();
        Product product = getById(id);

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

        log.info(user.getNameAndSurname() + " try to update product. Id : " + id);

        if (!(Role.ADMIN.equals(user.getRole()) || getById(id).getSellerId().equals(user.getId()))) {
            log.warn("User is not authorized to update this product");
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
        if (Role.ADMIN.equals(user.getRole()) || getById(id).getSellerId().equals(user.getId())) {
            productRepository.deleteById(id);
        } else {
            log.warn(user.getNameAndSurname() + " cannot delete this product.");
        }
    }

    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getByEmail(email);
    }

    private List<Product> getByComplexFilteringUseSpecification(ProductFilterRequest request) {
        Specification<Product> spec = Specification.where(null);

        if (request.subCategoryId() != null) {
            spec = spec.and(ProductSpecification.hasSubCategoryId(request.subCategoryId()));
        }

        if (request.specifications() != null && !(request.specifications().isEmpty())) {
            spec = spec.and(ProductSpecification.hasSpecifications(request.specifications()));
        }

        if (request.gender() != null) {
            spec = spec.and(ProductSpecification.hasGender(request.gender()));
        }

        if (request.rating() != null) {
            spec = spec.and(ProductSpecification.graterThanRating(request.rating()));
        }

        if (request.minPrice() != null || request.maxPrice() != null) {
            spec = spec.and(ProductSpecification.betweenPrice(
                    request.minPrice() == null ? 0 : request.minPrice(),
                    request.maxPrice() == null ? Double.MAX_VALUE : request.maxPrice()
                    ));
        }

        return productRepository.findAll(spec);
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