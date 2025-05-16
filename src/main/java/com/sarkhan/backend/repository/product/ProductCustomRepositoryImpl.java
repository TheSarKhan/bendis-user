package com.sarkhan.backend.repository.product;

import com.sarkhan.backend.dto.product.ProductFilterRequest;
import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.product.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final EntityManager entityManager;

    @Override
    public List<Product> getByComplexFiltering(ProductFilterRequest request) {
        Double rating = request.rating();
        if (rating == null) rating = 0.0;

        Double minPrice = request.minPrice();
        if (minPrice == null) minPrice = 0.0;

        Double maxPrice = request.maxPrice();
        if (maxPrice == null) maxPrice = Double.MAX_VALUE;

        List<Gender> genders = request.gender() == null
                ? Arrays.asList(Gender.values())
                : List.of(request.gender());

        Map<String, List<String>> specifications = request.specifications();

        StringBuilder sql = new StringBuilder("""
                select * from products
                where sub_category_id = :subCategoryId and
                rating >= :rating and
                case when discount_price is null or discount_price = 0
                then original_price else discount_price end >= :minPrice and
                case when discount_price is null or discount_price = 0
                then original_price else discount_price end <= :maxPrice and
                gender in :gender""");

        Map<String, Object> params = new HashMap<>();
        params.put("subCategoryId", request.subCategoryId());
        params.put("rating", rating);
        params.put("minPrice", minPrice);
        params.put("maxPrice", maxPrice);
        params.put("gender", genders);

        int index = 0;
        for (Map.Entry<String, List<String>> entry : specifications.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            if (values == null || values.isEmpty()) continue;

            String paramName = "param" + index;

            sql.append(" AND COALESCE(specifications ->> '")
                    .append(key.replace("'", "''"))
                    .append("', '') IN (:")
                    .append(paramName)
                    .append(")");

            params.put(paramName, values);
            index++;
        }

        Query query = entityManager.createNativeQuery(sql.toString(), Product.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }
}