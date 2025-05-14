package com.sarkhan.backend.service.product.items;

import com.sarkhan.backend.model.product.items.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    Category add(String name);

    List<Category> getAll();

    Category getById(Long id);

    Category getByName(String name);

    List<Category> searchByName(String name);

    Category update(Long id, String name);

    void delete(Long id);
}
