package com.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentCreateOrderRequest {

    @NotNull(message = "Order ID is required")
    private Integer orderId;

    private BigDecimal amount;

    private String currency;

    private String receipt;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}
