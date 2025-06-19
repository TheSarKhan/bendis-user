package com.sarkhan.backend.service.impl.product;

import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.product.items.ProductUserHistoryRepository;
import com.sarkhan.backend.repository.product.items.UserFavoriteProductRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.impl.product.util.UserUtil;
import com.sarkhan.backend.service.product.items.CategoryService;
import com.sarkhan.backend.service.product.items.SubCategoryService;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductUserHistoryRepository historyRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private SubCategoryService subCategoryService;
    @Mock
    private UserService userService;
    @Mock
    private UserFavoriteProductRepository favoriteRepository;
    @Mock
    private Executor executor;

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

    @Test
    void testGetForHomePage() {
        when(productRepository.getFamousProducts(any(PageRequest.class))).thenReturn(List.of(sampleProduct));
        when(productRepository.getDiscountedProducts(any(PageRequest.class))).thenReturn(List.of(sampleProduct));
        when(productRepository.getMostFavoriteProducts(any(PageRequest.class))).thenReturn(List.of(sampleProduct));
        when(productRepository.getFlushProducts(any(PageRequest.class))).thenReturn(List.of(sampleProduct));
        when(categoryService.getAll()).thenReturn(List.of());
        when(subCategoryService.getAll()).thenReturn(List.of());
        try (MockedStatic<UserUtil> mockedStatic = mockStatic(UserUtil.class)) {
            mockedStatic.when(() -> UserUtil.getCurrentUser(any(), any()))
                    .thenThrow(new AuthException("User doesn't login!!!"));

            assertNotNull(productService.getForHomePage());
        }
        verify(productRepository).getFamousProducts(any(PageRequest.class));
        verify(productRepository).getDiscountedProducts(any(PageRequest.class));
        verify(productRepository).getMostFavoriteProducts(any(PageRequest.class));
        verify(productRepository).getFlushProducts(any(PageRequest.class));
    }

    @Test
    void testGetAllFamousProducts() {
        when(productRepository.getFamousProducts()).thenReturn(List.of(sampleProduct));
        when(categoryService.getAll()).thenReturn(List.of());
        when(subCategoryService.getAll()).thenReturn(List.of());

        assertNotNull(productService.getAllFamousProducts());
        verify(productRepository).getFamousProducts();
    }

    @Test
    void testGetAllDiscountedProducts() {
        when(productRepository.getDiscountedProducts()).thenReturn(List.of(sampleProduct));
        when(categoryService.getAll()).thenReturn(List.of());
        when(subCategoryService.getAll()).thenReturn(List.of());

        assertNotNull(productService.getAllDiscountedProducts());
        verify(productRepository).getDiscountedProducts();
    }

    @Test
    void testGetAllMostFavoriteProducts() {
        when(productRepository.getMostFavoriteProducts()).thenReturn(List.of(sampleProduct));
        when(categoryService.getAll()).thenReturn(List.of());
        when(subCategoryService.getAll()).thenReturn(List.of());

        assertNotNull(productService.getAllMostFavoriteProducts());
        verify(productRepository).getMostFavoriteProducts();
    }

    @Test
    void testGetAllFlushProducts() {
        when(productRepository.getFlushProducts()).thenReturn(List.of(sampleProduct));
        when(categoryService.getAll()).thenReturn(List.of());
        when(subCategoryService.getAll()).thenReturn(List.of());

        assertNotNull(productService.getAllFlushProducts());
        verify(productRepository).getFlushProducts();
    }

    @Test
    void testGetByIdAndAddHistory() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        Product result;
        try (MockedStatic<UserUtil> mockedStatic = mockStatic(UserUtil.class)) {
            mockedStatic.when(() -> UserUtil.getCurrentUser(any(), any()))
                    .thenThrow(new AuthException("User doesn't login!!!"));

            result = productService.getByIdAndAddHistory(1L);
        }
        assertEquals("Phone", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetBySlug_whenExists() {
        when(productRepository.getBySlug("phone")).thenReturn(Optional.of(sampleProduct));

        Product result;
        try (MockedStatic<UserUtil> mockedStatic = mockStatic(UserUtil.class)) {
            mockedStatic.when(() -> UserUtil.getCurrentUser(any(), any()))
                    .thenThrow(new AuthException("User doesn't login!!!"));

            result = productService.getBySlug("phone");
        }
        assertEquals("Phone", result.getName());
        verify(productRepository).getBySlug("phone");
    }

    @Test
    void testGetBySlug_whenNotFound() {
        when(productRepository.getBySlug("unknown")).thenReturn(Optional.empty());

        Exception ex = assertThrows(NoSuchElementException.class, () -> productService.getBySlug("unknown"));
        assertEquals("Cannot find product by unknown slug.", ex.getMessage());
        verify(productRepository).getBySlug("unknown");
    }

    @Test
    void testGetBySellerId() {
        when(productRepository.getBySellerId(1L)).thenReturn(List.of(sampleProduct));
        when(categoryService.getAll()).thenReturn(List.of());
        when(subCategoryService.getAll()).thenReturn(List.of());

        assertNotNull(productService.getBySellerId(1L));
        verify(productRepository).getBySellerId(1L);
    }

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
}
