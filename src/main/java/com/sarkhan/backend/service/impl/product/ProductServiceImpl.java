package com.sarkhan.backend.service.impl.product;

import com.sarkhan.backend.dto.product.*;
import com.sarkhan.backend.mapper.ProductMapper;
import com.sarkhan.backend.model.enums.Color;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import com.sarkhan.backend.model.product.items.SubCategory;
import com.sarkhan.backend.model.product.items.UserFavoriteProduct;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.product.items.ProductUserHistoryRepository;
import com.sarkhan.backend.repository.product.items.UserFavoriteProductRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.product.ProductService;
import com.sarkhan.backend.service.product.items.CategoryService;
import com.sarkhan.backend.service.product.items.SubCategoryService;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.sarkhan.backend.service.impl.product.util.AsyncUtil.*;
import static com.sarkhan.backend.service.impl.product.util.ProductFilterUtil.getByComplexFilteringUseSpecification;
import static com.sarkhan.backend.service.impl.product.util.ProductImageUtil.deleteAllImages;
import static com.sarkhan.backend.service.impl.product.util.ProductImageUtil.uploadImages;
import static com.sarkhan.backend.service.impl.product.util.RecommendationUtil.getRecommendedProduct;
import static com.sarkhan.backend.service.impl.product.util.RecommendationUtil.partialShuffle;
import static com.sarkhan.backend.service.impl.product.util.UserUtil.addProductUserHistory;
import static com.sarkhan.backend.service.impl.product.util.UserUtil.getCurrentUser;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final ProductUserHistoryRepository historyRepository;

    private final CloudinaryService cloudinaryService;

    private final CategoryService categoryService;

    private final SubCategoryService subCategoryService;

    private final UserService userService;

    private final UserFavoriteProductRepository favoriteRepository;

    private final Executor executor;

    @Value("${product.recommend.maxProduct}")
    int recommendedProductMaxSize;

    @Value("${product.recommend.maxSwapDistance}")
    int maxSwapDistance;

    @Value("${product.recommend.shuffleProbability}")
    double shuffleProbability;

    @Value("${product.home-page-count}")
    int homePageCount;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductUserHistoryRepository historyRepository,
                              CloudinaryService cloudinaryService,
                              CategoryService categoryService,
                              SubCategoryService subCategoryService,
                              UserService userService, UserFavoriteProductRepository favoriteRepository,
                              @Qualifier("virtualExecutor") Executor executor) {
        this.productRepository = productRepository;
        this.historyRepository = historyRepository;
        this.cloudinaryService = cloudinaryService;
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
        this.userService = userService;
        this.favoriteRepository = favoriteRepository;
        this.executor = executor;
    }

    @Override
    @Async
    public CompletableFuture<Product> add(ProductRequest request, List<MultipartFile> images)
            throws IOException, AuthException {
        User user = getCurrentUser(userService, log);
        log.info(user.getFullName() + " try to create product");

        Product product = ProductMapper.toEntity(request, user);
        List<ColorAndSize> colorAndSizes = uploadImages(request, images, cloudinaryService, log);
        product.setColorAndSizes(colorAndSizes);

        log.info("Product create successfully.");
        return CompletableFuture.completedFuture(productRepository.save(product));
    }

    @Override
    public List<Product> getAll() {
        log.info("Someone try to get all products.");
        return productRepository.findAll();
    }

    @Override
    public ProductResponseForHomePage getForHomePage() {
        log.info("Someone try to get products for home page.");

        Pageable pageable = PageRequest.of(1, homePageCount + 20);

        return new ProductResponseForHomePage(
                shuffleAndDecreaseSize(productRepository.getFamousProducts(pageable)),
                shuffleAndDecreaseSize(productRepository.getDiscountedProducts(pageable)),
                shuffleAndDecreaseSize(productRepository.getMostFavoriteProducts(pageable)),
                shuffleAndDecreaseSize(productRepository.getFlushProducts(pageable)),
                recommendedProduct(),
                categoryService.getAll(),
                subCategoryService.getAll());
    }

    @Override
    public ProductResponseSimple getAllFamousProducts() {
        log.info("Someone try to get all famous products.");
        return new ProductResponseSimple(
                productRepository.getFamousProducts(),
                categoryService.getAll(),
                subCategoryService.getAll());
    }

    @Override
    public ProductResponseSimple getAllDiscountedProducts() {
        log.info("Someone try to get all discounted products.");
        return new ProductResponseSimple(
                productRepository.getDiscountedProducts(),
                categoryService.getAll(),
                subCategoryService.getAll());
    }

    @Override
    public ProductResponseSimple getAllMostFavoriteProducts() {
        log.info("Someone try to get all most favorite products.");
        return new ProductResponseSimple(
                productRepository.getMostFavoriteProducts(),
                categoryService.getAll(),
                subCategoryService.getAll());
    }

    @Override
    public ProductResponseSimple getAllFlushProducts() {
        log.info("Someone try to get all flush products.");
        return new ProductResponseSimple(
                productRepository.getFlushProducts(),
                categoryService.getAll(),
                subCategoryService.getAll());
    }

    @Override
    public ProductResponseSimple getAllRecommendedProduct() {
        log.info("Someone try to get all recommended products.");
        return new ProductResponseSimple(
                recommendedProduct(),
                categoryService.getAll(),
                subCategoryService.getAll());
    }

    @Override
    public Product getByIdAndAddHistory(Long id) {
        log.info("Someone try to get a product. Id : " + id);
        Product product = getById(id);
        addProductUserHistory(product, userService, historyRepository, log);
        return product;
    }

    @Override
    public Product getBySlug(String slug) {
        log.info("Someone try to get a product. Slug : " + slug);

        Product product = productRepository.getBySlug(slug).orElseThrow(() -> {
            log.info("Cannot find product by " + slug + " slug.");
            return new NoSuchElementException("Cannot find product by " + slug + " slug.");
        });

        addProductUserHistory(product, userService, historyRepository, log);
        return product;
    }

    @Override
    @Async
    public CompletableFuture<ProductResponseForSearchByName> searchByName(String name) {
        log.info("Someone try to get products. Name : {}", name);

        var productsFuture = getProductsByName(name, productRepository, executor);
        var categoriesFuture = getCategoriesByName(name, categoryService, executor);

        var subCategoriesFuture = getSubCategoriesByCategories(categoriesFuture, subCategoryService, executor);
        var searchedSubCategoriesFuture = getSubCategoriesByName(name, subCategoryService, executor);

        var combinedSubCategoriesFuture = combineSubCategories(
                subCategoriesFuture, searchedSubCategoriesFuture);

        var subCategoryProductsFuture = getProductsBySubCategories(combinedSubCategoriesFuture,
                productRepository, executor);

        var allCategoriesAndSubCategories = categoryAndSubCategoryGetAll(categoryService,
                subCategoryService, executor);

        return CompletableFuture.allOf(productsFuture, subCategoryProductsFuture, allCategoriesAndSubCategories)
                .thenApply(unused -> {
                    try {
                        Set<Product> products = new HashSet<>();
                        products.addAll(productsFuture.get());
                        products.addAll(subCategoryProductsFuture.get());

                        var allData = allCategoriesAndSubCategories.get();

                        return new ProductResponseForSearchByName(
                                name,
                                products.stream().toList(),
                                allData.categories(),
                                allData.subCategories()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to build response", e);
                    }
                });
    }

    @Override
    @Async
    public CompletableFuture<ProductResponseForSelectedSubCategoryAndComplexFilter> getBySubCategoryId(Long subCategoryId) {
        log.info("Someone try to get products. SubCategory id : " + subCategoryId);

        CompletableFuture<List<Product>> productsFuture = CompletableFuture.supplyAsync(
                () -> productRepository.getBySubCategoryId(subCategoryId), executor);

        CompletableFuture<List<Category>> categoriesFuture = CompletableFuture.supplyAsync(
                categoryService::getAll, executor);

        CompletableFuture<List<SubCategory>> subCategoriesFuture = CompletableFuture.supplyAsync(
                subCategoryService::getAll, executor);

        CompletableFuture<List<String>> specsFuture = CompletableFuture.supplyAsync(
                () -> subCategoryService.getById(subCategoryId).getSpecifications(), executor);

        CompletableFuture<List<String>> colorFuture = CompletableFuture.supplyAsync(
                () -> productRepository.getDistinctColorsBySubCategoryId(subCategoryId), executor);

        CompletableFuture<List<String>> sizeFuture = CompletableFuture.supplyAsync(
                () -> productRepository.getDistinctSizesBySubCategoryId(subCategoryId), executor);

        return CompletableFuture.allOf(productsFuture, categoriesFuture, subCategoriesFuture, specsFuture, colorFuture, sizeFuture)
                .thenApply(v -> {
                    try {
                        return new ProductResponseForSelectedSubCategoryAndComplexFilter(
                                productsFuture.get(),
                                categoriesFuture.get(),
                                subCategoriesFuture.get(),
                                specsFuture.get(),
                                colorFuture.get().stream()
                                        .map(Color::valueOf)
                                        .toList(),
                                sizeFuture.get(),
                                new ProductFilterRequest(subCategoryId, null, null,
                                        null, null, null, null,
                                        null));
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
    public CompletableFuture<ProductResponseForSelectedSubCategoryAndComplexFilter> getByComplexFiltering(ProductFilterRequest request) {
        log.info("Someone try to get product with complex params.");
        CompletableFuture<List<Product>> productsFuture = CompletableFuture.supplyAsync(() ->
                getByComplexFilteringUseSpecification(request, productRepository));

        CompletableFuture<List<Category>> categoriesFuture =
                CompletableFuture.supplyAsync(categoryService::getAll, executor);

        CompletableFuture<List<SubCategory>> subCategoriesFuture =
                CompletableFuture.supplyAsync(subCategoryService::getAll, executor);

        CompletableFuture<List<String>> specsFuture = CompletableFuture.supplyAsync(() ->
                subCategoryService.getById(request.subCategoryId()).getSpecifications(), executor);

        CompletableFuture<List<String>> colorFuture = CompletableFuture.supplyAsync(
                () -> productRepository.getDistinctColorsBySubCategoryId(request.subCategoryId()), executor);

        CompletableFuture<List<String>> sizeFuture = CompletableFuture.supplyAsync(
                () -> productRepository.getDistinctSizesBySubCategoryId(request.subCategoryId()), executor);

        return CompletableFuture.allOf(productsFuture, categoriesFuture, subCategoriesFuture, specsFuture, colorFuture, sizeFuture)
                .thenApply(unused -> {
                    try {
                        return new ProductResponseForSelectedSubCategoryAndComplexFilter(
                                productsFuture.get(),
                                categoriesFuture.get(),
                                subCategoriesFuture.get(),
                                specsFuture.get(),
                                colorFuture.get().stream()
                                        .map(Color::valueOf)
                                        .toList(),
                                sizeFuture.get(),
                                request);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public List<Product> getAllFavorite() throws AuthException {
        log.info("Someone try to get all favorite product.");
        return productRepository.getAllFavorite(getCurrentUser(userService, log).getId());
    }

    @Override
    public Product giveRating(Long id, Double rating) throws AuthException {
        User user = getCurrentUser(userService, log);
        log.warn("Someone try to give rating product but he/she doesn't login!!!");
        if (user == null) throw new AuthException("Someone try to give rating product but he/she doesn't login!!!");
        Product product = getById(id);

        log.info(user.getFullName() + " try to give rating. Product name : " + product.getName());

        Map<Long, Double> ratings = product.getRatings();
        if (ratings.containsKey(user.getId())) {
            log.warn(user.getFullName() + " try to give additional rating.");
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
        User user = getCurrentUser(userService, log);
        Product product = getById(id);

        log.info(user.getFullName() + " pres favorite button. Product name : " + product.getName());

        Long userId = user.getId();
        Optional<UserFavoriteProduct> favorite = favoriteRepository.
                getByProductIdAndUserId(product.getId(), userId);

        if (favorite.isPresent()) {
            log.info("User remove favorite.");
            product.setFavoriteCount(product.getFavoriteCount() - 1);
            favoriteRepository.delete(favorite.get());
        } else {
            log.info("User add favorite.");
            product.setFavoriteCount(product.getFavoriteCount() + 1);
            favoriteRepository.save(UserFavoriteProduct.builder().
                    userId(userId).productId(product.getId()).build());
        }
        return product;
    }

    @Override
    public Product update(Long id, ProductRequest request, List<MultipartFile> newImages)
            throws IOException, AuthException {
        Product oldProduct = ProductMapper.updateEntity(getById(id), request);
        User user = getCurrentUser(userService, log);
        if (user == null) {
            log.warn("Someone try to update product but he/she doesn't login!!!");
            throw new AuthException("Someone try to update product but he/she doesn't login!!!");
        }
        log.info(user.getFullName() + " try to update product. Id : " + id);

        if (!(Role.ADMIN.equals(user.getRole()) || getById(id).getSellerId().equals(user.getId()))) {
            log.warn("User is not authorized to update this product");
            throw new AccessDeniedException("You are not authorized to update this product");
        }

        Product product = ProductMapper.updateEntity(oldProduct, request);

        deleteAllImages(product, cloudinaryService);

        List<ColorAndSize> colorAndSizes = uploadImages(request, newImages, cloudinaryService, log);

        product.setColorAndSizes(colorAndSizes);

        log.info("Product update successfully.");
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) throws AuthException {
        User user = getCurrentUser(userService, log);
        log.warn("Someone try to delete product but he/she doesn't login!!!");
        if (user == null) throw new AuthException("Someone try to delete product but he/she doesn't login!!!");
        log.warn(user.getFullName() + " delete product. Id : " + id);
        if (Role.ADMIN.equals(user.getRole()) || getById(id).getSellerId().equals(user.getId())) {
            productRepository.deleteById(id);
        } else {
            log.warn(user.getFullName() + " cannot delete this product.");
        }
    }

    public Product getById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> {
            log.info("Cannot find product by " + id + " id.");
            return new NoSuchElementException("Cannot find product by " + id + " id.");
        });
    }

    private List<Product> shuffleAndDecreaseSize(List<Product> products) {
        return partialShuffle(products, shuffleProbability, recommendedProductMaxSize).
                stream().
                limit(homePageCount).
                toList();
    }

    private List<Product> recommendedProduct() {
        return getRecommendedProduct(historyRepository, productRepository,
                subCategoryService, userService,
                log, recommendedProductMaxSize,
                shuffleProbability, maxSwapDistance);
    }
}