package com.sarkhan.backend.service.impl.product.items;

import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.repository.product.items.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();
    }

    @Test
    void testAdd() {
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

        Category result = categoryService.add("Electronics");

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testGetAll() {
        when(categoryRepository.findAll()).thenReturn(List.of(sampleCategory,sampleCategory));

        List<Category> result = categoryService.getAll();

        assertEquals(2, result.size());
        assertEquals("Electronics", result.getFirst().getName());
        verify(categoryRepository).findAll();
    }

    @Test
    void testGetById_whenExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        Category result = categoryService.getById(1L);

        assertEquals("Electronics", result.getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void testGetById_whenNotFound() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NoSuchElementException.class, () -> categoryService.getById(2L));
        assertEquals("Cannot find any category with 2 id.", ex.getMessage());

        verify(categoryRepository).findById(2L);
    }

    @Test
    void testGetByName_whenExists() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(sampleCategory));

        Category result = categoryService.getByName("Electronics");

        assertEquals(1L, result.getId());
        verify(categoryRepository).findByName("Electronics");
    }

    @Test
    void testSearchByName() {
        when(categoryRepository.searchByName("Elect")).thenReturn(List.of(sampleCategory));

        List<Category> result = categoryService.searchByName("Elect");

        assertEquals(1, result.size());
        assertEquals("Electronics", result.getFirst().getName());
        verify(categoryRepository).searchByName("Elect");
    }

    @Test
    void testUpdate() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

        Category updated = categoryService.update(1L, "Updated");

        assertEquals("Updated", updated.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testDelete() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }
}
