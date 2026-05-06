package com.ecommerce.config;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        List<Product> products = List.of(
            new Product(
                "Wireless Headphones",
                "Comfortable over-ear headphones with active noise cancellation.",
                BigDecimal.valueOf(129.99),
                "Electronics",
                "https://images.unsplash.com/photo-1511376777868-611b54f68947?auto=format&fit=crop&w=800&q=80",
                32
            ),
            new Product(
                "Classic Leather Wallet",
                "Premium genuine leather wallet with multiple card slots and RFID protection.",
                BigDecimal.valueOf(49.99),
                "Accessories",
                "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=800&q=80",
                18
            ),
            new Product(
                "Electric Standing Desk",
                "Height-adjustable desk designed for comfort and productivity.",
                BigDecimal.valueOf(399.99),
                "Home Office",
                "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?auto=format&fit=crop&w=800&q=80",
                7
            ),
            new Product(
                "Stainless Steel Water Bottle",
                "Reusable insulated water bottle that keeps drinks cold for 24 hours.",
                BigDecimal.valueOf(24.99),
                "Fitness",
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80",
                60
            )
        );

        productRepository.saveAll(products);
    }
}
