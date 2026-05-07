package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@Valid @RequestBody AddToCartRequest request) {
        CartItem updatedCartItem = cartService.addToCart(
            request.getUserId(),
            request.getProductId(),
            request.getQuantity()
        );

        return ResponseEntity.ok(updatedCartItem);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartService.CartSummaryResponse> getCart(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getCartSummaryByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<CartService.CartSummaryResponse> getCartByQuery(@RequestParam Integer userId) {
        return getCart(userId);
    }

    @PutMapping("/update")
    public ResponseEntity<CartItem> updateCart(@Valid @RequestBody UpdateCartRequest request) {
        return ResponseEntity.ok(
            cartService.updateCartItemQuantity(request.getCartId(), request.getQuantity())
        );
    }

    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<RemoveCartResponse> removeFromCart(@PathVariable Integer cartId) {
        CartService.CartSummaryResponse updatedCart = cartService.removeCartItem(cartId);
        return ResponseEntity.ok(new RemoveCartResponse("Cart item removed successfully.", updatedCart));
    }

    public static class AddToCartRequest {
        @NotNull(message = "User ID is required")
        private Integer userId;

        @NotNull(message = "Product ID is required")
        private Integer productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public static class UpdateCartRequest {
        @NotNull(message = "Cart ID is required")
        private Integer cartId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        public Integer getCartId() {
            return cartId;
        }

        public void setCartId(Integer cartId) {
            this.cartId = cartId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public static class RemoveCartResponse {
        private final String message;
        private final CartService.CartSummaryResponse cart;

        public RemoveCartResponse(String message, CartService.CartSummaryResponse cart) {
            this.message = message;
            this.cart = cart;
        }

        public String getMessage() {
            return message;
        }

        public CartService.CartSummaryResponse getCart() {
            return cart;
        }
    }
}
