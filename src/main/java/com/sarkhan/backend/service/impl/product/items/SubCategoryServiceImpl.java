package com.sarkhan.backend.service.impl.product.items;

import com.sarkhan.backend.dto.product.items.SubCategoryRequest;
import com.sarkhan.backend.model.product.items.SubCategory;
import com.sarkhan.backend.repository.product.items.SubCategoryRepository;
import com.sarkhan.backend.service.product.items.SubCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {
    private final SubCategoryRepository repository;

    @Override
    public SubCategory add(SubCategoryRequest request) {
        log.info("Someone try to create sub category. Name : " + request.name());
        SubCategory subCategory = SubCategory.builder()
                .name(request.name())
                .categoryId(request.categoryId())
                .specifications(request.specification())
                .build();
        return repository.save(subCategory);
    }

    @Override
    public List<SubCategory> getAll() {
        log.info("Someone try to get all sub categories.");
        return repository.findAll();
    }

    @Override
    public SubCategory getById(Long id) {
        log.info("Someone try to get a sub category. Id : " + id);
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Cannot find subCategory with " + id + " id .");
            return new NoSuchElementException("Cannot find subCategory with " + id + " id .");
        });
    }

    @Override
    public SubCategory getByName(String name) {
        log.info("Someone try to get a sub category. Name : " + name);
        return repository.findByName(name).orElseThrow(() -> {
            log.warn("Cannot find subCategory with " + name + " name .");
            return new NoSuchElementException("Cannot find subCategory with " + name + " name .");
        });
    }

    @Override
    public List<SubCategory> searchByName(String name) {
        log.info("Someone try to search sub categories. Name : " + name);
        return repository.searchByName(name);
    }

    @Override
    public List<SubCategory> getByCategoryId(Long categoryId) {
        log.info("Someone try to get sub categories. Category id : " + categoryId);
        return repository.getByCategoryId(categoryId);
    }

    @Override
    public SubCategory update(Long id, SubCategoryRequest request) {
        SubCategory subCategory = getById(id);

        log.info("Someone try to update sub category. Old sub category : " + subCategory);

        subCategory.setName(request.name());
        subCategory.setCategoryId(request.categoryId());
        subCategory.setSpecifications(request.specification());

        return repository.save(subCategory);
    }

    @Override
    public void delete(Long id) {
        log.warn("Someone try to delete sub category. Id : " + id);
        repository.deleteById(id);
    }

    @Override
    public List<Long> getCategoryIdsBySubCategoryIds(List<Long> subCategoryIds) {
        return repository.getCategoryIdsBySubCategoryIds(subCategoryIds);
    }

    @Override
    public List<SubCategory> getByCategoryIds(List<Long> categoryIds) {
        return repository.getByCategoryIds(categoryIds);
    }
}
