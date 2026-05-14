package com.ecommerce.service;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.CheckoutResponse;
import com.ecommerce.dto.OrderSummaryResponse;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.PaymentMethod;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(
        UserRepository userRepository,
        CartRepository cartRepository,
        CartItemRepository cartItemRepository,
        ProductRepository productRepository,
        OrderRepository orderRepository
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public CheckoutResponse checkout(Integer userId, CheckoutRequest request) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Checkout request is required");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = cartRepository.findByUserAndStatus(user, Cart.CartStatus.ACTIVE)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress(buildShippingAddress(request));
        order.setOrderItems(new ArrayList<>());

        List<Product> updatedProducts = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart item product is missing");
            }

            Integer quantity = cartItem.getQuantity();
            if (quantity == null || quantity < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid cart item quantity");
            }

            Integer stockQuantity = product.getStockQuantity();
            if (stockQuantity == null || stockQuantity < quantity) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient stock for product: " + product.getName()
                );
            }

            BigDecimal unitPrice = cartItem.getPriceAtTime() != null
                ? cartItem.getPriceAtTime()
                : product.getPrice();

            if (unitPrice == null) {
                unitPrice = BigDecimal.ZERO;
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));
            order.addOrderItem(orderItem);

            product.setStockQuantity(stockQuantity - quantity);
            updatedProducts.add(product);
        }

        order.recalculateTotalAmount();
        Order savedOrder = orderRepository.save(order);
        productRepository.saveAll(updatedProducts);

        cartItemRepository.deleteByCartId(cart.getCartId());
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);

        return CheckoutResponse.success(OrderSummaryResponse.from(savedOrder));
    }

    private String buildShippingAddress(CheckoutRequest request) {
        return String.join(
            ", ",
            request.getName(),
            request.getPhone(),
            request.getAddress(),
            request.getCity(),
            request.getState(),
            request.getPincode()
        );
    }
}
