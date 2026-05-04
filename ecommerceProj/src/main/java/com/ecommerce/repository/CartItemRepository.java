package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // Find all items in a cart
    List<CartItem> findByCart(Cart cart);

    // Find specific item in cart
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    // Delete all items in a cart
    void deleteByCart(Cart cart);

    // Count items in cart
    long countByCart(Cart cart);
}