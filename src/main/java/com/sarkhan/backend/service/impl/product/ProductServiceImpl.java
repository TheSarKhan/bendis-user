package com.sarkhan.backend.service.impl.product;

import com.sarkhan.backend.dto.product.*;
import com.sarkhan.backend.mapper.comment.CommentMapper;
import com.sarkhan.backend.mapper.product.ProductMapper;
import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.enums.Color;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.order.OrderItem;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import com.sarkhan.backend.model.product.items.Plus;
import com.sarkhan.backend.model.product.items.SubCategory;
import com.sarkhan.backend.model.product.items.UserFavoriteProduct;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.comment.CommentRepository;
import com.sarkhan.backend.repository.order.OrderRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.product.items.PlusRepository;
import com.sarkhan.backend.repository.product.items.ProductUserHistoryRepository;
import com.sarkhan.backend.repository.product.items.UserFavoriteProductRepository;
import com.sarkhan.backend.repository.user.SellerRepository;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

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

    private final SellerRepository sellerRepository;

    private final PlusRepository plusRepository;

    private final CommentRepository commentRepository;

    private final OrderRepository orderRepository;

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
                              @Qualifier("taskExecutor") Executor executor,
                              SellerRepository sellerRepository,
                              PlusRepository plusRepository, CommentRepository commentRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.historyRepository = historyRepository;
        this.cloudinaryService = cloudinaryService;
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
        this.userService = userService;
        this.favoriteRepository = favoriteRepository;
        this.executor = executor;
        this.sellerRepository = sellerRepository;
        this.plusRepository = plusRepository;
        this.commentRepository = commentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Product add(ProductRequest request, List<MultipartFile> images)
            throws IOException, AuthException {
        User user = getCurrentUser(userService, log);
        log.info(user.getFullName() + " try to create product");

        Product product = ProductMapper.toEntity(request, user);
        List<ColorAndSize> colorAndSizes = uploadImages(request, images, cloudinaryService, log);
        product.setColorAndSizes(colorAndSizes);

        log.info("Product create successfully.");
        return productRepository.save(product);
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
                recommendedProduct()
        );
    }

    @Override
    public List<ProductResponseForGroupOfProduct> getAllFamousProducts() {
        log.info("Someone try to get all famous products.");
        return mapProductsToProductsResponse(productRepository.getFamousProducts());
    }

    @Override
    public List<ProductResponseForGroupOfProduct> getAllDiscountedProducts() {
        log.info("Someone try to get all discounted products.");
        return mapProductsToProductsResponse(productRepository.getDiscountedProducts());
    }

    @Override
    public List<ProductResponseForGroupOfProduct> getAllMostFavoriteProducts() {
        log.info("Someone try to get all most favorite products.");
        return mapProductsToProductsResponse(productRepository.getMostFavoriteProducts());
    }

    @Override
    public List<ProductResponseForGroupOfProduct> getAllFlushProducts() {
        log.info("Someone try to get all flush products.");
        return mapProductsToProductsResponse(productRepository.getFlushProducts());
    }

    @Override
    public List<ProductResponseForGroupOfProduct> getAllRecommendedProduct() {
        log.info("Someone try to get all recommended products.");
        return mapProductsToProductsResponse(recommendedProduct());
    }

    @Override
    public ProductResponseForGetSingleOne getByIdAndAddHistory(Long id) {
        log.info("Someone try to get a product. Id : " + id);
        Product product = getById(id);
        addProductUserHistory(product, userService, historyRepository, log);
        return mapProductToResponse(product);
    }

    @Override
    public ProductResponseForGetSingleOne getBySlug(String slug) {
        log.info("Someone try to get a product. Slug : " + slug);

        Product product = productRepository.getBySlug(slug).orElseThrow(() -> {
            log.info("Cannot find product by " + slug + " slug.");
            return new NoSuchElementException("Cannot find product by " + slug + " slug.");
        });

        addProductUserHistory(product, userService, historyRepository, log);
        return mapProductToResponse(product);
    }

    @Override
    public List<Long> getMyDeliveredProductId() throws AuthException {
        User currentUser = getCurrentUser(userService, log);
        List<Order> byUserId = orderRepository.findByUserId(currentUser.getId());
        return byUserId.stream()
                .filter(order -> OrderStatus.DELIVERED.equals(order.getOrderStatus()))
                .map(Order::getOrderItemList)
                .reduce((a, b) -> {
                    a.addAll(b);
                    return a;
                })
                .orElseThrow(() -> new NoSuchElementException("Cannot find any order for this user."))
                .stream().map(OrderItem::getProductId).toList();
    }

    @Override
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

        SecurityContext context = SecurityContextHolder.getContext();

        return CompletableFuture.allOf(productsFuture, subCategoryProductsFuture)
                .thenApply(unused -> {
                    SecurityContextHolder.setContext(context);
                    try {
                        Set<Product> products = new HashSet<>();
                        products.addAll(productsFuture.get());
                        products.addAll(subCategoryProductsFuture.get());

                        return new ProductResponseForSearchByName(
                                name,
                                mapProductsToProductsResponse(
                                        products.stream().toList())
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to build response" + e, e);
                    }
                });
    }

    @Override
    public ProductResponseForSelectedSubCategoryAndComplexFilter getBySubCategoryId(Long subCategoryId) {
        log.info("Someone try to get products. SubCategory id : " + subCategoryId);

        List<Product> productsBySubCategory = productRepository.getBySubCategoryId(subCategoryId);

        List<String> specifications = subCategoryService.getById(subCategoryId).getSpecifications();

        List<ColorAndSize> colorAndSizes = productsBySubCategory
                .stream()
                .map(Product::getColorAndSizes)
                .reduce((a, b) -> {
                    a.addAll(b);
                    return a;
                })
                .orElse(new ArrayList<>());

        List<ProductResponseForGroupOfProduct> products = mapProductsToProductsResponse(productsBySubCategory);

        List<Color> uniqueColors = colorAndSizes
                .stream()
                .map(ColorAndSize::getColor)
                .collect(Collectors.toSet())
                .stream()
                .toList();

        var availableSizes = getUniqueSizes(colorAndSizes);

        ProductFilterRequest productFilter = new ProductFilterRequest(subCategoryId, null, null,
                null, null, null, null,
                null);
        return new ProductResponseForSelectedSubCategoryAndComplexFilter(
                products,
                specifications,
                uniqueColors,
                availableSizes,
                productFilter);
    }


    @Override
    public List<ProductResponseForGroupOfProduct> getBySellerId(Long sellerId) {
        log.info("Someone try to get products. Seller id : " + sellerId);
        return mapProductsToProductsResponse(productRepository.getBySellerId(sellerId));
    }

    @Override
    public CompletableFuture<ProductResponseForSelectedSubCategoryAndComplexFilter> getByComplexFiltering(ProductFilterRequest request) {
        log.info("Someone try to get product with complex params.");
        CompletableFuture<List<Product>> productsFuture = CompletableFuture.supplyAsync(() ->
                getByComplexFilteringUseSpecification(request, productRepository));

        CompletableFuture<List<Product>> productsBySubCategoryFuture = CompletableFuture.supplyAsync(
                () -> productRepository.getBySubCategoryId(request.subCategoryId()), executor);

        CompletableFuture<List<String>> specsFuture = CompletableFuture.supplyAsync(() ->
                subCategoryService.getById(request.subCategoryId()).getSpecifications(), executor);

        SecurityContext context = SecurityContextHolder.getContext();

        return CompletableFuture.allOf(productsFuture, specsFuture, productsBySubCategoryFuture)
                .thenApply(unused -> {
                    SecurityContextHolder.setContext(context);
                    try {
                        List<ColorAndSize> colorAndSizes = productsBySubCategoryFuture.get()
                                .stream()
                                .map(Product::getColorAndSizes)
                                .reduce((a, b) -> {
                                    a.addAll(b);
                                    return a;
                                })
                                .orElse(new ArrayList<>());

                        List<String> availableSizes =  getUniqueSizes(colorAndSizes);
                        return new ProductResponseForSelectedSubCategoryAndComplexFilter(
                                mapProductsToProductsResponse(
                                        productsFuture.get()),
                                specsFuture.get(),
                                colorAndSizes
                                        .stream()
                                        .map(ColorAndSize::getColor)
                                        .collect(Collectors.toSet())
                                        .stream()
                                        .toList(),
                                availableSizes,
                                request);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private List<String> getUniqueSizes(List<ColorAndSize> colorAndSizes) {
        HashSet<String> sizes = new HashSet<>();
        for (ColorAndSize colorAndSize : colorAndSizes){
            Set<String> sizeKeys = colorAndSize.getSizeStockMap().keySet();
            sizes.addAll(sizeKeys);
        }
        return sizes.stream().toList();
    }

    @Override
    public List<Product> getAllFavorite() throws AuthException {
        log.info("Someone try to get all favorite product.");
        return productRepository.getAllFavorite(getCurrentUser(userService, log).getId());
    }

    @Override
    public void giveRating(Long id, Double rating) throws AuthException {
        User user = getCurrentUser(userService, log);
        Product product = getById(id);

        log.info(user.getFullName() + " try to give rating. Product name : " + product.getName());

        Map<Long, Double> ratings = product.getRatings();
        if (ratings.containsKey(user.getId())) {
            log.warn(user.getFullName() + " try to give additional rating.");
            mapProductToResponse(product);
            return;
        }
        Double oldRating = product.getRating();
        Double newRating = (oldRating * ratings.size() + rating) / (ratings.size() + 1);
        ratings.put(user.getId(), rating);

        product.setRating(newRating);
        product.setRatings(ratings);
        productRepository.save(product);
    }

    @Override
    public ProductResponseForGetSingleOne toggleFavorite(Long id) throws AuthException {
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
        productRepository.save(product);
        return mapProductToResponse(product);
    }

    @Override
    public ProductResponseForGetSingleOne update(Long id, ProductRequest request, List<MultipartFile> newImages)
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
        return mapProductToResponse(productRepository.save(product));
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

    @Override
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

    private ProductResponseForGetSingleOne mapProductToResponse(Product product) {
        SubCategory subCategory = subCategoryService.getById(product.getSubCategoryId());
        Seller seller = sellerRepository.findById(product.getSellerId()).orElseThrow();
        List<Plus> pluses = plusRepository.findAllById(product.getPluses());
        List<Comment> byProductId = commentRepository.getByProductId(product.getId());
        boolean isFavorite = false;
        try {
            User currentUser = getCurrentUser(userService, log);
            isFavorite = favoriteRepository.getByProductIdAndUserId(product.getId(), currentUser.getId()).isPresent();
        }catch (AuthException e){
            log.warn("User is not login. Cannot check favorite status.");
        }
        return ProductMapper.productToProductResponseForGetSingleOne(
                product,
                subCategory,
                seller,
                pluses,
                CommentMapper.mapCommentsToCommentResponses(byProductId),
                isFavorite);
    }

    private List<ProductResponseForGroupOfProduct> mapProductsToProductsResponse(List<Product> products) {
        try {
            User currentUser = getCurrentUser(userService, log);
            Map<Product, Boolean> productResponseMap =
                    mapProductsToResponseWhenUserLogin(products, currentUser);
            return ProductMapper.mapListOfProductToResponse(productResponseMap);
        } catch (AuthException e) {
            return ProductMapper.mapListOfProductToResponse(products);
        }
    }

    private Map<Product, Boolean> mapProductsToResponseWhenUserLogin(List<Product> products, User currentUser) {
        HashMap<Product, Boolean> productIsFavoriteMap = new HashMap<>();
        for (Product product : products)
            productIsFavoriteMap.put(
                    product,
                    favoriteRepository.getByProductIdAndUserId(
                            product.getId(),
                            currentUser.getId()).isPresent());
        return productIsFavoriteMap;
    }
}