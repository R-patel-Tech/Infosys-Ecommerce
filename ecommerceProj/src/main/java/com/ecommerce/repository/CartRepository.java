package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Cart.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    // Find active cart for a user
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);

    // Find cart by user (assuming one active cart per user)
    Optional<Cart> findByUser(User user);

    // Check if user has an active cart
    boolean existsByUserAndStatus(User user, CartStatus status);

    // Find all carts for a user
    @Query("SELECT c FROM Cart c WHERE c.user = :user ORDER BY c.updatedAt DESC")
    java.util.List<Cart> findAllByUserOrderByUpdatedAtDesc(@Param("user") User user);
}