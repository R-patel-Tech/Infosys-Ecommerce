package com.ecommerce.specification;

import com.ecommerce.entity.Product;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> matchesQuery(String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        }

        String pattern = "%" + query.trim().toLowerCase() + "%";
        return (root, queryBuilder, criteriaBuilder) -> criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("name")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("description")), pattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("category")), pattern)
        );
    }

    public static Specification<Product> hasCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return null;
        }

        String normalizedCategory = category.trim().toLowerCase();
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(criteriaBuilder.lower(root.<String>get("category")), normalizedCategory);
    }

    public static Specification<Product> hasMinimumPrice(BigDecimal minPrice) {
        if (minPrice == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
            criteriaBuilder.greaterThanOrEqualTo(root.<BigDecimal>get("price"), minPrice);
    }

    public static Specification<Product> hasMaximumPrice(BigDecimal maxPrice) {
        if (maxPrice == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
            criteriaBuilder.lessThanOrEqualTo(root.<BigDecimal>get("price"), maxPrice);
    }

    public static Specification<Product> isInStock(Boolean inStock) {
        if (inStock == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> inStock
            ? criteriaBuilder.greaterThan(root.<Integer>get("stockQuantity"), 0)
            : criteriaBuilder.equal(root.<Integer>get("stockQuantity"), 0);
    }
}
