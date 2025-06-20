package com.sarkhan.backend.service.impl.product.items;

import com.sarkhan.backend.dto.product.items.SubCategoryRequest;
import com.sarkhan.backend.model.product.items.SubCategory;
import com.sarkhan.backend.repository.product.items.SubCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubCategoryServiceImplTest {

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @InjectMocks
    private SubCategoryServiceImpl subCategoryService;

    private SubCategory sampleSubCategory;

    private SubCategoryRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new SubCategoryRequest("Phones", 1L, List.of("brand", "model"));
        sampleSubCategory = SubCategory.builder()
                .id(1L)
                .name("Phones")
                .categoryId(1L)
                .specifications(List.of("brand", "model"))
                .build();
    }

    @Test
    void testAdd() {
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(sampleSubCategory);

        SubCategory result = subCategoryService.add(sampleRequest);

        assertNotNull(result);
        assertEquals("Phones", result.getName());
        verify(subCategoryRepository).save(any(SubCategory.class));
    }

    @Test
    void testGetAll() {
        when(subCategoryRepository.findAll()).thenReturn(List.of(sampleSubCategory, sampleSubCategory));

        List<SubCategory> result = subCategoryService.getAll();

        assertEquals(2, result.size());
        assertEquals("Phones", result.getFirst().getName());
        verify(subCategoryRepository).findAll();
    }

    @Test
    void testGetById_whenExists() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(sampleSubCategory));

        SubCategory result = subCategoryService.getById(1L);

        assertEquals("Phones", result.getName());
        verify(subCategoryRepository).findById(1L);
    }

    @Test
    void testGetById_whenNotFound() {
        when(subCategoryRepository.findById(2L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NoSuchElementException.class, () -> subCategoryService.getById(2L));
        assertEquals("Cannot find subCategory with 2 id .", ex.getMessage());

        verify(subCategoryRepository).findById(2L);
    }

    @Test
    void testGetByName_whenExists() {
        when(subCategoryRepository.findByName("Phones")).thenReturn(Optional.of(sampleSubCategory));

        SubCategory result = subCategoryService.getByName("Phones");

        assertEquals(1L, result.getId());
        verify(subCategoryRepository).findByName("Phones");
    }

    @Test
    void testGetByName_whenNotFound() {
        when(subCategoryRepository.findByName("Laptops")).thenReturn(Optional.empty());

        Exception ex = assertThrows(NoSuchElementException.class, () -> subCategoryService.getByName("Laptops"));
        assertEquals("Cannot find subCategory with Laptops name .", ex.getMessage());

        verify(subCategoryRepository).findByName("Laptops");
    }

    @Test
    void testSearchByName() {
        when(subCategoryRepository.searchByName("Pho")).thenReturn(List.of(sampleSubCategory));

        List<SubCategory> result = subCategoryService.searchByName("Pho");

        assertEquals(1, result.size());
        assertEquals("Phones", result.getFirst().getName());
        verify(subCategoryRepository).searchByName("Pho");
    }

    @Test
    void testGetByCategoryId() {
        when(subCategoryRepository.getByCategoryId(1L)).thenReturn(List.of(sampleSubCategory));

        List<SubCategory> result = subCategoryService.getByCategoryId(1L);

        assertEquals(1, result.size());
        assertEquals("Phones", result.getFirst().getName());
        verify(subCategoryRepository).getByCategoryId(1L);
    }

    @Test
    void testUpdate() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(sampleSubCategory));
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(sampleSubCategory);

        SubCategory updated = subCategoryService.update(1L, new SubCategoryRequest("Updated", 2L, List.of("new")));

        assertEquals("Updated", updated.getName());
        assertEquals(2L, updated.getCategoryId());
        assertEquals(List.of("new"), updated.getSpecifications());
        verify(subCategoryRepository).save(any(SubCategory.class));
    }

    @Test
    void testDelete() {
        doNothing().when(subCategoryRepository).deleteById(1L);

        subCategoryService.delete(1L);

        verify(subCategoryRepository).deleteById(1L);
    }

    @Test
    void testGetCategoryIdsBySubCategoryIds() {
        when(subCategoryRepository.getCategoryIdsBySubCategoryIds(List.of(1L, 2L)))
                .thenReturn(List.of(1L, 1L));

        List<Long> result = subCategoryService.getCategoryIdsBySubCategoryIds(List.of(1L, 2L));

        assertEquals(2, result.size());
        verify(subCategoryRepository).getCategoryIdsBySubCategoryIds(List.of(1L, 2L));
    }

    @Test
    void testGetByCategoryIds() {
        when(subCategoryRepository.getByCategoryIds(List.of(1L, 2L)))
                .thenReturn(List.of(sampleSubCategory));

        List<SubCategory> result = subCategoryService.getByCategoryIds(List.of(1L, 2L));

        assertEquals(1, result.size());
        assertEquals("Phones", result.getFirst().getName());
        verify(subCategoryRepository).getByCategoryIds(List.of(1L, 2L));
    }
}
