package com.sarkhan.backend.service.product.items;

import com.sarkhan.backend.model.product.items.SubCategory;

import java.util.List;

public interface SubCategoryService {
    SubCategory add(String name, Long categoryId, List<String> specification);

    List<SubCategory> getAll();

    SubCategory getById(Long id);

    SubCategory getByName(String name);

    List<SubCategory> searchByName(String name);

    List<SubCategory> getByCategoryId(Long categoryId);

    SubCategory update(Long id, String name, Long categoryId, List<String> specification);

    void delete(Long id);
}
