package com.ecommerce.dto;

import com.ecommerce.entity.OrderItem;
import java.math.BigDecimal;

public class OrderItemResponse {

    private final Integer orderItemId;
    private final Integer productId;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal subtotal;

    public OrderItemResponse(
        Integer orderItemId,
        Integer productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
    ) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
            item.getOrderItemId(),
            item.getProduct() == null ? null : item.getProduct().getProductId(),
            item.getProduct() == null ? null : item.getProduct().getName(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getSubtotal()
        );
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
