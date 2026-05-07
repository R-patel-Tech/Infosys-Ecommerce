package com.ecommerce.controller;

import com.ecommerce.service.CartService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
    RequestMethod.POST,
    RequestMethod.OPTIONS
})
public class CheckoutController {

    private final CartService cartService;

    public CheckoutController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> checkout(@PathVariable @NotNull Integer userId) {
        cartService.checkoutCart(userId);
        return ResponseEntity.ok("Order placed successfully");
    }
}
