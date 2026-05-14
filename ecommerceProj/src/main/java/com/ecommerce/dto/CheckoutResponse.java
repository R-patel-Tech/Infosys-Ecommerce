package com.ecommerce.dto;

public class CheckoutResponse {

    private final String message;
    private final OrderSummaryResponse order;

    public CheckoutResponse(String message, OrderSummaryResponse order) {
        this.message = message;
        this.order = order;
    }

    public static CheckoutResponse success(OrderSummaryResponse order) {
        return new CheckoutResponse("Order placed successfully", order);
    }

    public String getMessage() {
        return message;
    }

    public OrderSummaryResponse getOrder() {
        return order;
    }
}
