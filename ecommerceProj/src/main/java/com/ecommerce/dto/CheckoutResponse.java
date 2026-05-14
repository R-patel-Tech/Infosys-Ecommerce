package com.ecommerce.dto;

public class CheckoutResponse {

    private final String message;
    private final OrderSummaryResponse order;
    private final PaymentCreateOrderResponse payment;

    public CheckoutResponse(String message, OrderSummaryResponse order) {
        this(message, order, null);
    }

    public CheckoutResponse(String message, OrderSummaryResponse order, PaymentCreateOrderResponse payment) {
        this.message = message;
        this.order = order;
        this.payment = payment;
    }

    public static CheckoutResponse success(OrderSummaryResponse order) {
        return new CheckoutResponse("Order placed successfully", order);
    }

    public static CheckoutResponse success(OrderSummaryResponse order, PaymentCreateOrderResponse payment) {
        return new CheckoutResponse("Order placed successfully", order, payment);
    }

    public String getMessage() {
        return message;
    }

    public OrderSummaryResponse getOrder() {
        return order;
    }

    public PaymentCreateOrderResponse getPayment() {
        return payment;
    }
}
