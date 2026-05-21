package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Cart.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUserUserId(Integer userId);

    // Find active cart for a user
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);

    // Find cart by user (assuming one active cart per user)
}
