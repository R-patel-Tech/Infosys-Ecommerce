package com.ecommerce.controller;

import com.ecommerce.entity.CartItem;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
    RequestMethod.GET,
    RequestMethod.POST,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.OPTIONS
})
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(
            request.getUserId(),
            request.getProductId(),
            request.getQuantity()
        );

        return ResponseEntity.ok("Item added to cart");
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
    public ResponseEntity<String> updateCart(@Valid @RequestBody UpdateCartRequest request) {
        cartService.updateCartItemQuantity(request.getCartId(), request.getQuantity());
        return ResponseEntity.ok("Cart updated successfully");
    }

    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Integer cartId) {
        cartService.removeCartItem(cartId);
        return ResponseEntity.ok("Cart item removed successfully");
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared");
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

}
