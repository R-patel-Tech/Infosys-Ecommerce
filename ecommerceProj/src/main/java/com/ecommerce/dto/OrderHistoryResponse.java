package com.ecommerce.dto;

import com.ecommerce.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderHistoryResponse {

    private final Integer orderId;
    private final LocalDateTime orderDate;
    private final BigDecimal totalAmount;
    private final String status;
    private final List<OrderItemHistoryResponse> orderItems;

    public OrderHistoryResponse(
        Integer orderId,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String status,
        List<OrderItemHistoryResponse> orderItems
    ) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems = orderItems;
    }

    public static OrderHistoryResponse from(Order order) {
        List<OrderItemHistoryResponse> items = order.getOrderItems().stream()
            .map(OrderItemHistoryResponse::from)
            .toList();

        return new OrderHistoryResponse(
            order.getOrderId(),
            order.getOrderDate(),
            order.getTotalAmount(),
            order.getOrderStatus() == null ? null : order.getOrderStatus().name(),
            items
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

    public String getStatus() {
        return status;
    }

    public List<OrderItemHistoryResponse> getOrderItems() {
        return orderItems;
    }
}
