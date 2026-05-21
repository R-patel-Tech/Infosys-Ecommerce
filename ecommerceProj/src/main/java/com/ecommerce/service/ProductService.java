package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.specification.ProductSpecifications;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> findProducts(String query, String category, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice must be less than or equal to maxPrice");
        }

        Specification<Product> specification = null;
        specification = addSpecification(specification, ProductSpecifications.matchesQuery(query));
        specification = addSpecification(specification, ProductSpecifications.hasCategory(category));
        specification = addSpecification(specification, ProductSpecifications.hasMinimumPrice(minPrice));
        specification = addSpecification(specification, ProductSpecifications.hasMaximumPrice(maxPrice));
        specification = addSpecification(specification, ProductSpecifications.isInStock(inStock));

        return productRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public Product getProduct(Integer id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @Transactional
    public Product updateProduct(Integer id, Product product) {
        Product existingProduct = getProduct(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setStockQuantity(product.getStockQuantity());
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product existingProduct = getProduct(id);
        productRepository.delete(existingProduct);
    }

    @Transactional
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    private static Specification<Product> addSpecification(Specification<Product> current, Specification<Product> next) {
        if (next == null) {
            return current;
        }

        return current == null ? next : current.and(next);
    }
}
