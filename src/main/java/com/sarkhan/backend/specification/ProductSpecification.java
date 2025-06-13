package com.sarkhan.backend.specification;

import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.product.Product;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
        return (root, query, cb) -> {
            List<Map<String, String>> combinations = generateCombinations(filters);
            List<Predicate> orPredicates = new ArrayList<>();

            for (Map<String, String> combination : combinations) {
                List<Predicate> andPredicates = new ArrayList<>();

                for (Map.Entry<String, String> entry : combination.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    String path = String.format("$.%s ? (@ == \"%s\")", key, value);

                    Expression<Boolean> jsonPathExists = cb.function(
                            "jsonb_path_exists",
                            Boolean.class,
                            root.get("specifications"),
                            cb.literal(path)
                    );

                    andPredicates.add(cb.isTrue(jsonPathExists));
                }

                orPredicates.add(cb.and(andPredicates.toArray(new Predicate[0])));
            }

            return cb.or(orPredicates.toArray(new Predicate[0]));
        };
    }


    private static List<Map<String, String>> generateCombinations(Map<String, List<String>> filters) {
        List<Map<String, String>> combinations = new ArrayList<>();
        combinations.add(new HashMap<>());

        for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            List<Map<String, String>> newCombinations = new ArrayList<>();

            for (Map<String, String> existingCombo : combinations) {
                for (String value : values) {
                    Map<String, String> newCombo = new HashMap<>(existingCombo);
                    newCombo.put(key, value);
                    newCombinations.add(newCombo);
                }
            }

            combinations = newCombinations;
        }

        return combinations;
    }

}
