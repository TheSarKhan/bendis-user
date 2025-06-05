package com.sarkhan.backend.util;

import com.sarkhan.backend.dto.product.items.CategoryAndSubCategoryGetAll;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.model.product.items.SubCategory;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.product.items.CategoryService;
import com.sarkhan.backend.service.product.items.SubCategoryService;
import com.sarkhan.backend.specification.ProductSpecification;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class AsyncUtil {

    public static CompletableFuture<List<Product>> getProductsByName(String name, ProductRepository productRepository, Executor executor) {
        return CompletableFuture.supplyAsync(() -> productRepository.findAll(ProductSpecification.searchTitle(name)), executor);
    }

    public static CompletableFuture<List<Category>> getCategoriesByName(String name, CategoryService categoryService, Executor executor) {
        return CompletableFuture.supplyAsync(() -> categoryService.searchByName(name), executor);
    }

    public static CompletableFuture<Set<SubCategory>> getSubCategoriesByCategories(CompletableFuture<List<Category>> categoriesFuture, SubCategoryService subCategoryService, Executor executor) {
        return categoriesFuture.thenCompose(categories -> {
            List<Long> ids = categories.stream().map(Category::getId).toList();
            return CompletableFuture.supplyAsync(() -> new HashSet<>(subCategoryService.getByCategoryIds(ids)), executor);
        });
    }

    public static CompletableFuture<Set<SubCategory>> getSubCategoriesByName(String name, SubCategoryService subCategoryService, Executor executor) {
        return CompletableFuture.supplyAsync(() -> new HashSet<>(subCategoryService.searchByName(name)), executor);
    }

    public static CompletableFuture<Set<SubCategory>> combineSubCategories(
            CompletableFuture<Set<SubCategory>> f1,
            CompletableFuture<Set<SubCategory>> f2) {

        return f1.thenCombine(f2, (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        });
    }

    public static CompletableFuture<List<Product>> getProductsBySubCategories(
            CompletableFuture<Set<SubCategory>> subCategoriesFuture, ProductRepository productRepository, Executor executor) {

        return subCategoriesFuture.thenCompose(subCategories -> {
            List<CompletableFuture<List<Product>>> futures = subCategories.stream()
                    .map(sc -> CompletableFuture.supplyAsync(() -> productRepository.getBySubCategoryId(sc.getId()), executor))
                    .toList();

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(unused -> futures.stream()
                            .flatMap(f -> f.join().stream())
                            .collect(Collectors.toList()));
        });
    }

    public static CompletableFuture<CategoryAndSubCategoryGetAll> categoryAndSubCategoryGetAll(
            CategoryService categoryService, SubCategoryService subCategoryService, Executor executor) {

        CompletableFuture<List<Category>> allCategoriesFuture = CompletableFuture.supplyAsync(categoryService::getAll, executor);
        CompletableFuture<List<SubCategory>> allSubCategoriesFuture = CompletableFuture.supplyAsync(subCategoryService::getAll, executor);

        return CompletableFuture.allOf(allCategoriesFuture, allSubCategoriesFuture)
                .thenApply(unused -> {
                    try {
                        return new CategoryAndSubCategoryGetAll(
                                allCategoriesFuture.get(),
                                allSubCategoriesFuture.get());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
