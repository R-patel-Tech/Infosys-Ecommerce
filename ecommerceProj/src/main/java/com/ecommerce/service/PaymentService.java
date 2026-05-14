package com.ecommerce.service;

import com.ecommerce.config.RazorpayProperties;
import com.ecommerce.dto.OrderSummaryResponse;
import com.ecommerce.dto.PaymentCreateOrderRequest;
import com.ecommerce.dto.PaymentCreateOrderResponse;
import com.ecommerce.dto.PaymentVerifyRequest;
import com.ecommerce.dto.PaymentVerifyResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.repository.OrderRepository;
import com.razorpay.Entity;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final RazorpayProperties razorpayProperties;

    public PaymentService(OrderRepository orderRepository, RazorpayProperties razorpayProperties) {
        this.orderRepository = orderRepository;
        this.razorpayProperties = razorpayProperties;
    }

    public PaymentCreateOrderResponse createGatewayOrderFor(Order order) {
        return createGatewayOrderFor(order, null);
    }

    public PaymentCreateOrderResponse createGatewayOrderFor(Order order, PaymentCreateOrderRequest request) {
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is required");
        }

        boolean demoMode = !isConfigured();
        if (!demoMode) {
            ensureKeysConfigured();
        }

        long amountInPaise = toPaise(order.getTotalAmount());
        String currency = normalizeCurrency(request == null ? null : request.getCurrency());
        String receipt = normalizeReceipt(request == null ? null : request.getReceipt(), order.getOrderId());

        try {
            String razorpayOrderId;

            if (demoMode) {
                razorpayOrderId = "demo_" + order.getOrderId() + "_" + receipt;
            } else {
                RazorpayClient client = new RazorpayClient(razorpayProperties.getKeyId(), razorpayProperties.getKeySecret());
                JSONObject options = new JSONObject();
                options.put("amount", amountInPaise);
                options.put("currency", currency);
                options.put("receipt", receipt);
                options.put("payment_capture", 1);

                Entity createdOrder = client.orders.create(options);
                razorpayOrderId = createdOrder.get("id").toString();
            }

            order.setOrderPaymentStatus("PENDING");
            orderRepository.save(order);

            return new PaymentCreateOrderResponse(
                order.getOrderId(),
                razorpayOrderId,
                amountInPaise,
                currency,
                receipt,
                razorpayProperties.getKeyId(),
                razorpayProperties.getBrandName(),
                razorpayProperties.getDescription(),
                razorpayProperties.getLogoUrl(),
                demoMode
            );
        } catch (RazorpayException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to create payment order");
        }
    }

    @Transactional
    public PaymentCreateOrderResponse createOrder(PaymentCreateOrderRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment request is required");
        }

        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return createGatewayOrderFor(order, request);
    }

    @Transactional
    public PaymentVerifyResponse verifyPayment(PaymentVerifyRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification payload is required");
        }

        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!isConfigured()) {
            if (request.getPaymentStatus() != null && request.getPaymentStatus().equalsIgnoreCase("success")) {
                String paymentId = isBlank(request.getRazorpayPaymentId())
                    ? "demo_payment_" + order.getOrderId()
                    : request.getRazorpayPaymentId();
                order.setPaymentId(paymentId);
                order.setOrderPaymentStatus("PAID");
                order.setOrderStatus(OrderStatus.PAID);
                Order savedOrder = orderRepository.save(order);

                return new PaymentVerifyResponse(
                    "Payment verified successfully",
                    savedOrder.getOrderId(),
                    savedOrder.getPaymentId(),
                    savedOrder.getOrderPaymentStatus(),
                    OrderSummaryResponse.from(savedOrder)
                );
            }

            markFailed(order);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment failed");
        }

        if (request.getPaymentStatus() != null && !request.getPaymentStatus().equalsIgnoreCase("success")) {
            markFailed(order);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment failed");
        }

        ensureKeysConfigured();

        if (isBlank(request.getRazorpayOrderId())
            || isBlank(request.getRazorpayPaymentId())
            || isBlank(request.getRazorpaySignature())) {
            markFailed(order);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment data is incomplete");
        }

        String expectedSignature = generateSignature(
            request.getRazorpayOrderId(),
            request.getRazorpayPaymentId(),
            razorpayProperties.getKeySecret()
        );

        if (!MessageDigest.isEqual(
            expectedSignature.getBytes(StandardCharsets.UTF_8),
            request.getRazorpaySignature().getBytes(StandardCharsets.UTF_8)
        )) {
            markFailed(order);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment signature");
        }

        order.setPaymentId(request.getRazorpayPaymentId());
        order.setOrderPaymentStatus("PAID");
        order.setOrderStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);

        return new PaymentVerifyResponse(
            "Payment verified successfully",
            savedOrder.getOrderId(),
            savedOrder.getPaymentId(),
            savedOrder.getOrderPaymentStatus(),
            OrderSummaryResponse.from(savedOrder)
        );
    }

    private void markFailed(Order order) {
        order.setOrderPaymentStatus("FAILED");
        orderRepository.save(order);
    }

    private void ensureKeysConfigured() {
        if (!isConfigured()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Razorpay keys are not configured");
        }
    }

    private boolean isConfigured() {
        return !isBlank(razorpayProperties.getKeyId()) && !isBlank(razorpayProperties.getKeySecret());
    }

    private long toPaise(BigDecimal amount) {
        if (amount == null) {
            return 0L;
        }

        return amount.multiply(BigDecimal.valueOf(100))
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact();
    }

    private String normalizeCurrency(String currency) {
        String requested = isBlank(currency) ? razorpayProperties.getCurrency() : currency.trim();
        return requested.toUpperCase();
    }

    private String normalizeReceipt(String receipt, Integer orderId) {
        if (!isBlank(receipt)) {
            return receipt.trim();
        }

        return "order_" + orderId;
    }

    private String generateSignature(String razorpayOrderId, String razorpayPaymentId, String secret) {
        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to verify payment signature");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
