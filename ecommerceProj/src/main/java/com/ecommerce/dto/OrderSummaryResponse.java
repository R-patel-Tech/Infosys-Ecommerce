package com.ecommerce.dto;

import com.ecommerce.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderSummaryResponse {

    private final Integer orderId;
    private final LocalDateTime orderDate;
    private final BigDecimal totalAmount;
    private final String orderStatus;
    private final String paymentMethod;
    private final String shippingAddress;
    private final int itemCount;
    private final List<OrderItemResponse> items;

    public OrderSummaryResponse(
        Integer orderId,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String orderStatus,
        String paymentMethod,
        String shippingAddress,
        int itemCount,
        List<OrderItemResponse> items
    ) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
        this.itemCount = itemCount;
        this.items = items;
    }

    public static OrderSummaryResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
            .map(OrderItemResponse::from)
            .toList();

        int totalItems = itemResponses.stream()
            .mapToInt(item -> item.getQuantity() == null ? 0 : item.getQuantity())
            .sum();

        return new OrderSummaryResponse(
            order.getOrderId(),
            order.getOrderDate(),
            order.getTotalAmount(),
            order.getOrderStatus() == null ? null : order.getOrderStatus().name(),
            order.getPaymentMethod() == null ? null : order.getPaymentMethod().name(),
            order.getShippingAddress(),
            totalItems,
            itemResponses
        );
    }

    public Integer getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public int getItemCount() {
        return itemCount;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
