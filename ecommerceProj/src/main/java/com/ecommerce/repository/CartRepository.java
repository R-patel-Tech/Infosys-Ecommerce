package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Cart.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUserUserId(Integer userId);

    // Find active cart for a user
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);

    // Find cart by user (assuming one active cart per user)
    Optional<Cart> findByUser(User user);

    // Check if user has an active cart
    boolean existsByUserAndStatus(User user, CartStatus status);

    // Find all carts for a user
    @Query("SELECT c FROM Cart c WHERE c.user = :user ORDER BY c.updatedAt DESC")
    List<Cart> findAllByUserOrderByUpdatedAtDesc(@Param("user") User user);

    Optional<Cart> findByUserAndProduct(User user, Product product);

    List<Cart> findByProductOrderByAddedAtDesc(Product product);

    @Modifying
    @Query("update Cart c set c.status = :status, c.totalAmount = :totalAmount where c.cartId = :cartId")
    int updateStatusAndTotalAmount(
        @Param("cartId") Integer cartId,
        @Param("status") CartStatus status,
        @Param("totalAmount") java.math.BigDecimal totalAmount
    );
}
