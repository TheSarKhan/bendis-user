package com.sarkhan.backend.service.impl.product;

import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.product.items.ProductUserHistoryRepository;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.impl.product.util.RecommendationUtil;
import com.sarkhan.backend.service.product.items.CategoryService;
import com.sarkhan.backend.service.product.items.SubCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SubCategoryService subCategoryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id(1L)
                .name("Phone")
                .slug("phone")
                .build();
    }

    @Test
    void testGetAll() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getAll();

        assertEquals(1, result.size());
        assertEquals("Phone", result.getFirst().getName());
        verify(productRepository).findAll();
    }

//    @Test
//    void testGetForHomePage() {
//        securityContextHolderConfigWrong();
//        when(productRepository.getFamousProducts(any(PageRequest.class))).thenReturn(List.of(sampleProduct));
//        when(productRepository.getDiscountedProducts(any(PageRequest.class))).thenReturn(List.of(sampleProduct));
//        when(productRepository.getMostFavoriteProducts(any(PageRequest.class))).thenReturn(List.of(sampleProduct));
//        when(productRepository.getFlushProducts(any(PageRequest.class))).thenReturn(List.of(sampleProduct));
//        when(categoryService.getAll()).thenReturn(List.of());
//        when(subCategoryService.getAll()).thenReturn(List.of());
//        try (MockedStatic<RecommendationUtil> recommendationStatic =
//                     mockStatic(RecommendationUtil.class)) {
//            recommendationStatic.when(() -> RecommendationUtil.getRecommendedProduct(
//                    Mockito.any(ProductUserHistoryRepository.class),
//                    Mockito.any(ProductRepository.class),
//                    Mockito.any(SubCategoryService.class),
//                    Mockito.any(UserService.class),
//                    Mockito.any(Logger.class),
//                    Mockito.anyInt(),
//                    Mockito.anyDouble(),
//                    Mockito.anyInt()
//            )).thenReturn(null);
//
//            assertNotNull(productService.getForHomePage());
//        }
//        verify(productRepository).getFamousProducts(any(PageRequest.class));
//        verify(productRepository).getDiscountedProducts(any(PageRequest.class));
//        verify(productRepository).getMostFavoriteProducts(any(PageRequest.class));
//        verify(productRepository).getFlushProducts(any(PageRequest.class));
//    }

//    @Test
//    void testGetAllFamousProducts() {
//        securityContextHolderConfigCorrectly();
//        when(productRepository.getFamousProducts()).thenReturn(List.of(sampleProduct));
//        when(categoryService.getAll()).thenReturn(List.of());
//        when(subCategoryService.getAll()).thenReturn(List.of());
//
//        assertNotNull(productService.getAllFamousProducts());
//        verify(productRepository).getFamousProducts();
//    }
//
//    @Test
//    void testGetAllDiscountedProducts() {
//        securityContextHolderConfigCorrectly();
//        when(productRepository.getDiscountedProducts()).thenReturn(List.of(sampleProduct));
//        when(categoryService.getAll()).thenReturn(List.of());
//        when(subCategoryService.getAll()).thenReturn(List.of());
//
//        assertNotNull(productService.getAllDiscountedProducts());
//        verify(productRepository).getDiscountedProducts();
//    }
//
//    @Test
//    void testGetAllMostFavoriteProducts() {
//        securityContextHolderConfigCorrectly();
//        when(productRepository.getMostFavoriteProducts()).thenReturn(List.of(sampleProduct));
//        when(categoryService.getAll()).thenReturn(List.of());
//        when(subCategoryService.getAll()).thenReturn(List.of());
//
//        assertNotNull(productService.getAllMostFavoriteProducts());
//        verify(productRepository).getMostFavoriteProducts();
//    }
//
//    @Test
//    void testGetAllFlushProducts() {
//        securityContextHolderConfigCorrectly();
//        when(productRepository.getFlushProducts()).thenReturn(List.of(sampleProduct));
//        when(categoryService.getAll()).thenReturn(List.of());
//        when(subCategoryService.getAll()).thenReturn(List.of());
//
//        assertNotNull(productService.getAllFlushProducts());
//        verify(productRepository).getFlushProducts();
//    }

    @Test
    void testGetBySlug_whenNotFound() {
        when(productRepository.getBySlug("unknown")).thenReturn(Optional.empty());

        Exception ex = assertThrows(NoSuchElementException.class, () -> productService.getBySlug("unknown"));
        assertEquals("Cannot find product by unknown slug.", ex.getMessage());
        verify(productRepository).getBySlug("unknown");
    }

//    @Test
//    void testGetBySellerId() {
//        securityContextHolderConfigCorrectly();
//        when(productRepository.getBySellerId(1L)).thenReturn(List.of(sampleProduct));
//        when(categoryService.getAll()).thenReturn(List.of());
//        when(subCategoryService.getAll()).thenReturn(List.of());
//
//        assertNotNull(productService.getBySellerId(1L));
//        verify(productRepository).getBySellerId(1L);
//    }

    @Test
    void testGetById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.getById(1L);

        assertEquals("Phone", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetById_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NoSuchElementException.class, () -> productService.getById(99L));
        assertEquals("Cannot find product by 99 id.", ex.getMessage());
        verify(productRepository).findById(99L);
    }

    private static void securityContextHolderConfigCorrectly() {
        String userEmail = "user@example.com";
        securityContextHolderConfig(userEmail);
    }

    private static void securityContextHolderConfigWrong() {
        String anonymousUser = "anonymousUser";
        securityContextHolderConfig(anonymousUser);
    }

    private static void securityContextHolderConfig(String userEmail) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userEmail);

        SecurityContextHolder.setContext(securityContext);
    }
}
