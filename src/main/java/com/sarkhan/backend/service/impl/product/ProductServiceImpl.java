package com.sarkhan.backend.service.impl.product;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.dto.product.*;
import com.sarkhan.backend.mapper.ProductMapper;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.model.product.items.Color;
import com.sarkhan.backend.model.product.items.ProductUserHistory;
import com.sarkhan.backend.model.product.items.SubCategory;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.product.items.ProductUserHistoryRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.product.ProductService;
import com.sarkhan.backend.service.product.items.CategoryService;
import com.sarkhan.backend.service.product.items.SubCategoryService;
import com.sarkhan.backend.specification.ProductSpecification;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final ProductUserHistoryRepository historyRepository;

    private final CloudinaryService cloudinaryService;

    private final CategoryService categoryService;

    private final SubCategoryService subCategoryService;

    private final UserService userService;

    private final Executor executor;

    @Value("${product.recommend.maxProduct}")
    Long recommendedProductMaxSize;

    @Value("${product.recommend.maxSwapDistance}")
    Long maxSwapDistance;

    @Value("${product.recommend.shuffleProbability}")
    double shuffleProbability;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductUserHistoryRepository historyRepository,
                              CloudinaryService cloudinaryService,
                              CategoryService categoryService,
                              SubCategoryService subCategoryService,
                              UserService userService,
                              @Qualifier("virtualExecutor") Executor executor) {
        this.productRepository = productRepository;
        this.historyRepository = historyRepository;
        this.cloudinaryService = cloudinaryService;
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
        this.userService = userService;
        this.executor = executor;
    }

    @Override
    @Async
    public CompletableFuture<Product> add(ProductRequest request, List<MultipartFile> images) throws IOException, AuthException {
        User user = getCurrentUser();
        log.warn("Someone try to add product but he/she doesn't login!!!");
        if (user == null) throw new AuthException("Someone try to add product but he/she doesn't login!!!");
        log.info(user.getNameAndSurname() + " try to create product");

        Product product = ProductMapper.toEntity(request, user);

        List<Color> colors = uploadImages(request, images);

        product.setColors(colors);

        log.info("Product create successfully.");
        return CompletableFuture.completedFuture(productRepository.save(product));
    }

    @Override
    public ProductResponseForGetAll getAll() {
        log.info("Someone try to get all products.");
        return new ProductResponseForGetAll(
                productRepository.findAll(),
                categoryService.getAll(),
                subCategoryService.getAll(),
                getRecommendedProduct());
    }

    @Override
    public Product getById(Long id) {
        log.info("Someone try to get a product. Id : " + id);

        Product product = productRepository.findById(id).orElseThrow(() -> {
            log.info("Cannot find product by " + id + " id.");
            return new NoSuchElementException("Cannot find product by " + id + " id.");
        });

        addProductUserHistory(product);
        return product;
    }

    @Override
    public Product getBySlug(String slug) {
        log.info("Someone try to get a product. Slug : " + slug);

        Product product = productRepository.getBySlug(slug).orElseThrow(() -> {
            log.info("Cannot find product by " + slug + " slug.");
            return new NoSuchElementException("Cannot find product by " + slug + " slug.");
        });

        addProductUserHistory(product);
        return product;
    }

    @Override
    @Async
    public CompletableFuture<ProductResponseForSearchByName> searchByName(String name) {
        log.info("Someone try to get products. Name : " + name);

        CompletableFuture<List<Product>> productsFuture = CompletableFuture.supplyAsync(
                () -> productRepository.findAll(ProductSpecification.searchTitle(name)), executor);

        CompletableFuture<List<Category>> categoriesFuture = CompletableFuture.supplyAsync(
                () -> categoryService.searchByName(name), executor);

        CompletableFuture<List<Category>> allCategoriesFuture =
                CompletableFuture.supplyAsync(categoryService::getAll, executor);

        CompletableFuture<List<SubCategory>> allSubCategoriesFuture =
                CompletableFuture.supplyAsync(subCategoryService::getAll, executor);

        CompletableFuture<Set<SubCategory>> subCategoriesFuture = categoriesFuture.thenCompose(categories -> {
            List<CompletableFuture<List<SubCategory>>> futures = categories.stream()
                    .map(category -> CompletableFuture.supplyAsync(
                            () -> subCategoryService.getByCategoryId(category.getId()), executor))
                    .toList();

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(unused -> futures.stream()
                            .flatMap(future -> future.join().stream())
                            .collect(Collectors.toSet()));
        });

        CompletableFuture<Set<SubCategory>> searchedSubCategoriesFuture = CompletableFuture.supplyAsync(
                () -> new HashSet<>(subCategoryService.searchByName(name)), executor);

        CompletableFuture<Set<SubCategory>> combinedSubCategoriesFuture = subCategoriesFuture.thenCombine(
                searchedSubCategoriesFuture,
                (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                });

        CompletableFuture<List<Product>> subCategoryProductsFuture = combinedSubCategoriesFuture.thenCompose(subCategories -> {
            List<CompletableFuture<List<Product>>> futures = subCategories.stream()
                    .map(subCategory -> CompletableFuture.supplyAsync(
                            () -> productRepository.getBySubCategoryId(subCategory.getId()), executor))
                    .toList();

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(unused -> futures.stream()
                            .flatMap(future -> future.join().stream())
                            .collect(Collectors.toList()));
        });

        return CompletableFuture.allOf(productsFuture, subCategoryProductsFuture, allCategoriesFuture, allSubCategoriesFuture)
                .thenApply(unused -> {
                    Set<Product> products = new HashSet<>();
                    try {
                        products.addAll(productsFuture.get());
                        products.addAll(subCategoryProductsFuture.get());

                        return new ProductResponseForSearchByName(
                                name,
                                products.stream().toList(),
                                allCategoriesFuture.get(),
                                allSubCategoriesFuture.get());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    @Async
    public CompletableFuture<ProductResponseForSelectedSubCategory> getBySubCategoryId(Long subCategoryId) {
        log.info("Someone try to get products. SubCategory id : " + subCategoryId);

        CompletableFuture<List<Product>> productsFuture = CompletableFuture.supplyAsync(
                () -> productRepository.getBySubCategoryId(subCategoryId), executor);

        CompletableFuture<List<Category>> categoriesFuture = CompletableFuture.supplyAsync(
                categoryService::getAll, executor);

        CompletableFuture<List<SubCategory>> subCategoriesFuture = CompletableFuture.supplyAsync(
                subCategoryService::getAll, executor);

        CompletableFuture<List<String>> specsFuture = CompletableFuture.supplyAsync(
                () -> subCategoryService.getById(subCategoryId).getSpecifications(), executor);

        return CompletableFuture.allOf(productsFuture, categoriesFuture, subCategoriesFuture, specsFuture)
                .thenApply(v -> {
                    try {
                        return new ProductResponseForSelectedSubCategory(
                                productsFuture.get(),
                                categoriesFuture.get(),
                                subCategoriesFuture.get(),
                                specsFuture.get(),
                                new ProductFilterRequest(subCategoryId, null, null, null, null, null));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    @Override
    public ProductResponseForGetBySellerId getBySellerId(Long sellerId) {
        log.info("Someone try to get products. Seller id : " + sellerId);
        return new ProductResponseForGetBySellerId(
                productRepository.getBySellerId(sellerId),
                categoryService.getAll(),
                subCategoryService.getAll());
    }

    @Override
    @Async
    public CompletableFuture<ProductResponseForSelectedSubCategory> getByComplexFiltering(ProductFilterRequest request) {
        log.info("Someone try to get product with complex params.");
        CompletableFuture<List<Product>> productsFuture = CompletableFuture.supplyAsync(() ->
                getByComplexFilteringUseSpecification(request));

        CompletableFuture<List<Category>> categoriesFuture =
                CompletableFuture.supplyAsync(categoryService::getAll, executor);

        CompletableFuture<List<SubCategory>> subCategoriesFuture =
                CompletableFuture.supplyAsync(subCategoryService::getAll, executor);

        CompletableFuture<List<String>> specsFuture = CompletableFuture.supplyAsync(() ->
                subCategoryService.getById(request.subCategoryId()).getSpecifications(), executor);

        return CompletableFuture.allOf(productsFuture, categoriesFuture, subCategoriesFuture, specsFuture)
                .thenApply(unused -> {
                    try {
                        return new ProductResponseForSelectedSubCategory(
                                productsFuture.get(),
                                categoriesFuture.get(),
                                subCategoriesFuture.get(),
                                specsFuture.get(),
                                request);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public Product giveRating(Long id, Double rating) throws AuthException {
        User user = getCurrentUser();
        log.warn("Someone try to give rating product but he/she doesn't login!!!");
        if (user == null) throw new AuthException("Someone try to give rating product but he/she doesn't login!!!");
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
    public Product toggleFavorite(Long id) throws AuthException {
        User user = getCurrentUser();
        log.warn("Someone try to add favorite product but he/she doesn't login!!!");
        if (user == null) throw new AuthException("Someone try to add favorite product but he/she doesn't login!!!");
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
    public Product update(Long id, ProductRequest request, List<MultipartFile> newImages) throws IOException, AuthException {
        Product oldProduct = ProductMapper.updateEntity(getById(id), request);
        User user = getCurrentUser();
        log.warn("Someone try to update product but he/she doesn't login!!!");
        if (user == null) throw new AuthException("Someone try to update product but he/she doesn't login!!!");
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
    public void delete(Long id) throws AuthException {
        User user = getCurrentUser();
        log.warn("Someone try to delete product but he/she doesn't login!!!");
        if (user == null) throw new AuthException("Someone try to delete product but he/she doesn't login!!!");
        log.warn(user.getNameAndSurname() + " delete product. Id : " + id);
        if (Role.ADMIN.equals(user.getRole()) || getById(id).getSellerId().equals(user.getId())) {
            productRepository.deleteById(id);
        } else {
            log.warn(user.getNameAndSurname() + " cannot delete this product.");
        }
    }

    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return email == null ? null : userService.getByEmail(email);
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

    private void addProductUserHistory(Product product) {
        User user = getCurrentUser();
        if (user != null) {
            ProductUserHistory history = ProductUserHistory.builder()
                    .userId(user.getId())
                    .subCategoryId(product.getSubCategoryId())
                    .build();
            historyRepository.save(history);
        }
    }

    private List<Product> getRecommendedProduct() {
        User user = getCurrentUser();
        if (user == null) return Collections.emptyList();

        Set<Product> products = new LinkedHashSet<>();
        List<Long> subCategoryIds = historyRepository
                .findTopSubCategoryIdsByUserId(user.getId());

        for (Long subCategoryId : subCategoryIds) {
            products.addAll(productRepository
                    .getBySubCategoryId(subCategoryId)
                    .stream()
                    .limit(recommendedProductMaxSize)
                    .collect(Collectors.toSet()));
        }

        if (products.size() < recommendedProductMaxSize) {
            List<Long> categoryIds = subCategoryService.getCategoryIdsBySubCategoryIds(subCategoryIds);

            for (Long categoryId : categoryIds) {
                for (SubCategory subCategory : subCategoryService.getByCategoryId(categoryId)) {
                    products.addAll(productRepository.getBySubCategoryId(subCategory.getId()));
                    if (products.size() >= recommendedProductMaxSize) break;
                }
                if (products.size() >= recommendedProductMaxSize) break;
            }
        }

        return partialShuffle(products.stream().toList());
    }


    private List<Product> partialShuffle(List<Product> input) {
        List<Product> result = new ArrayList<>(input);
        Random random = new Random();

        for (int i = 0; i < result.size(); i++) {
            if (random.nextDouble() < shuffleProbability) {
                int swapWith = i + random.nextInt(Math.min(maxSwapDistance.intValue(), result.size() - i));
                Collections.swap(result, i, swapWith);
            }
        }

        return result;
    }
}