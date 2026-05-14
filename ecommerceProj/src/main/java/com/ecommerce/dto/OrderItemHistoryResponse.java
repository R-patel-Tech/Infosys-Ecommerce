package com.ecommerce.dto;

import com.ecommerce.entity.OrderItem;
import java.math.BigDecimal;

public class OrderItemHistoryResponse {

    private final Integer orderItemId;
    private final String productName;
    private final String productImage;
    private final Integer quantity;
    private final BigDecimal price;

    public OrderItemHistoryResponse(
        Integer orderItemId,
        String productName,
        String productImage,
        Integer quantity,
        BigDecimal price
    ) {
        this.orderItemId = orderItemId;
        this.productName = productName;
        this.productImage = productImage;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItemHistoryResponse from(OrderItem item) {
        return new OrderItemHistoryResponse(
            item.getOrderItemId(),
            item.getProduct() == null ? null : item.getProduct().getName(),
            item.getProduct() == null ? null : item.getProduct().getImageUrl(),
            item.getQuantity(),
            item.getUnitPrice()
        );
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
