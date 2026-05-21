package com.ecommerce.controller;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.UpdateCartRequest;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(
    origins = "*",
    allowedHeaders = "*",
    methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS
    }
)
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", "Item added to cart"));
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
    public ResponseEntity<ApiResponse<String>> updateCart(@Valid @RequestBody UpdateCartRequest request) {
        cartService.updateCartItemQuantity(request.getCartId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Cart updated successfully", "Cart updated successfully"));
    }

    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<ApiResponse<String>> removeFromCart(@PathVariable Integer cartId) {
        cartService.removeCartItem(cartId);
        return ResponseEntity.ok(ApiResponse.success("Cart item removed successfully", "Cart item removed successfully"));
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<ApiResponse<String>> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", "Cart cleared"));
    }
}
