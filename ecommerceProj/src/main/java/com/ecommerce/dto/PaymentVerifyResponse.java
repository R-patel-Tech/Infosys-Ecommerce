package com.ecommerce.dto;

public class PaymentVerifyResponse {

    private final String message;
    private final Integer orderId;
    private final String paymentId;
    private final String orderPaymentStatus;
    private final OrderSummaryResponse order;

    public PaymentVerifyResponse(
        String message,
        Integer orderId,
        String paymentId,
        String orderPaymentStatus,
        OrderSummaryResponse order
    ) {
        this.message = message;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.orderPaymentStatus = orderPaymentStatus;
        this.order = order;
    }

    public String getMessage() {
        return message;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getOrderPaymentStatus() {
        return orderPaymentStatus;
    }

    public OrderSummaryResponse getOrder() {
        return order;
    }
}
