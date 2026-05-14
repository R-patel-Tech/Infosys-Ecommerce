package com.ecommerce.controller;

import com.ecommerce.dto.PaymentCreateOrderRequest;
import com.ecommerce.dto.PaymentCreateOrderResponse;
import com.ecommerce.dto.PaymentVerifyRequest;
import com.ecommerce.dto.PaymentVerifyResponse;
import com.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(
    origins = "*",
    allowedHeaders = "*",
    methods = {
        RequestMethod.POST,
        RequestMethod.OPTIONS
    }
)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<PaymentCreateOrderResponse> createOrder(
        @Valid @RequestBody PaymentCreateOrderRequest request
    ) {
        return ResponseEntity.ok(paymentService.createOrder(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerifyResponse> verify(
        @Valid @RequestBody PaymentVerifyRequest request
    ) {
        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }
}
