package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // Find all items in a cart
    List<CartItem> findByCart(Cart cart);

    List<CartItem> findByCart_CartId(Integer cartId);

    // Find specific item in cart
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    // Delete all items in a cart
    void deleteByCart(Cart cart);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from CartItem ci where ci.cart.cartId = :cartId")
    void deleteByCartId(@Param("cartId") Integer cartId);

    // Count items in cart
    long countByCart(Cart cart);
}
