package com.sarkhan.backend.specification;

import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.product.Product;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductSpecification {
    public static Specification<Product> searchTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            Expression<Double> function = criteriaBuilder.function(
                    "similarity", Double.class,
                    root.get("title"), criteriaBuilder.literal(title));
            query.orderBy(criteriaBuilder.desc(function));
            return criteriaBuilder.greaterThanOrEqualTo(function, 0.3);
        };
    }

    public static Specification<Product> hasSubCategoryId(Long subCategoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("subCategoryId"), subCategoryId);
    }

    public static Specification<Product> graterThanRating(Double rating) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), rating);
    }

    public static Specification<Product> betweenPrice(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            Expression<BigDecimal> effectivePrice = cb.coalesce(
                    root.get("discountedPrice"),
                    root.get("originalPrice")
            );
            return cb.between(effectivePrice, BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice));
        };
    }

    public static Specification<Product> hasGender(Gender gender){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("gender"), gender);
    }

    public static Specification<Product> hasSpecifications(Map<String, List<String>> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> globalPredicates = new ArrayList<>();

            for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();

                for (String value : values) {
                    String path = String.format("$.%s[*] ? (@ == \"%s\")", key, value);
                    Expression<Boolean> jsonContains = criteriaBuilder.function(
                            "jsonb_path_exists",
                            Boolean.class,
                            root.get("specifications"),
                            criteriaBuilder.literal(path)
                    );
                    globalPredicates.add(criteriaBuilder.isTrue(jsonContains));
                }
            }

            return criteriaBuilder.and(globalPredicates.toArray(new Predicate[0]));
        };
    }

}
