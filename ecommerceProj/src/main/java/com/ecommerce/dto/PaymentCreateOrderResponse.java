package com.ecommerce.dto;

public class PaymentCreateOrderResponse {

    private final Integer orderId;
    private final String razorpayOrderId;
    private final Long amount;
    private final String currency;
    private final String receipt;
    private final String keyId;
    private final String brandName;
    private final String description;
    private final String logoUrl;
    private final boolean demoMode;

    public PaymentCreateOrderResponse(
        Integer orderId,
        String razorpayOrderId,
        Long amount,
        String currency,
        String receipt,
        String keyId,
        String brandName,
        String description,
        String logoUrl,
        boolean demoMode
    ) {
        this.orderId = orderId;
        this.razorpayOrderId = razorpayOrderId;
        this.amount = amount;
        this.currency = currency;
        this.receipt = receipt;
        this.keyId = keyId;
        this.brandName = brandName;
        this.description = description;
        this.logoUrl = logoUrl;
        this.demoMode = demoMode;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getReceipt() {
        return receipt;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getDescription() {
        return description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public boolean isDemoMode() {
        return demoMode;
    }
}
