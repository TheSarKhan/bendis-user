package com.sarkhan.backend.service.impl.product.items;

import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.repository.product.items.CategoryRepository;
import com.sarkhan.backend.service.product.items.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category add(String name) {
        log.info("Someone try to create category. Name : " + name);
        Category category = Category.builder().name(name).build();
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAll() {
        log.info("Someone try to get all categories.");
        return categoryRepository.findAll();
    }

    @Override
    public Category getById(Long id) {
        log.info("Someone try to get a category. Id : " + id);
        return categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Cannot find any category with " + id + " id.");
            return new NoSuchElementException("Cannot find any category with " + id + " id.");
        });
    }

    @Override
    public Category getByName(String name) {
        log.info("Someone try to get a category. name : " + name);
        return categoryRepository.findByName(name).orElseThrow(() -> {
            log.error("Cannot find any category with " + name + " name.");
            return new NoSuchElementException("Cannot find any category with " + name + " name.");
        });
    }

    @Override
    public List<Category> searchByName(String name) {
        log.info("Someone try to search categories. name : " + name);
        return categoryRepository.searchByName(name);
    }

    @Override
    public Category update(Long id, String name) {
        Category category = getById(id);
        log.info("Someone try to update category. Old Category : " + category);

        category.setName(name);

        log.info("New Category : " + category);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        log.warn("Someone try to delete id : " + id);
        categoryRepository.deleteById(id);
    }
}
