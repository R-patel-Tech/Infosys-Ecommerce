package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(
        CartRepository cartRepository,
        CartItemRepository cartItemRepository,
        UserRepository userRepository,
        ProductRepository productRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Cart createCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public Cart getCartById(Integer cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public List<Cart> getCartsByUser(User user) {
        return cartRepository.findAllByUserOrderByUpdatedAtDesc(user);
    }

    public List<Cart> getCartsByProduct(Product product) {
        return cartRepository.findByProductOrderByAddedAtDesc(product);
    }

    public Cart getActiveCartByUserId(Integer userId) {
        User user = requireUser(userId);

        return cartRepository.findByUserAndStatus(user, Cart.CartStatus.ACTIVE)
            .orElseGet(() -> new Cart(user));
    }

    @Transactional
    public Cart getCartWithItemsByUserId(Integer userId) {
        User user = requireUser(userId);

        return cartRepository.findByUserAndStatus(user, Cart.CartStatus.ACTIVE)
            .orElseGet(() -> new Cart(user));
    }

    @Transactional
    public CartSummaryResponse getCartSummaryByUserId(Integer userId) {
        Cart cart = getCartWithItemsByUserId(userId);
        if (cart.getCartId() == null) {
            return new CartSummaryResponse(List.of(), BigDecimal.ZERO, 0);
        }

        List<CartItem> items = cartItemRepository.findByCart_CartId(cart.getCartId());

        List<CartItemSummary> itemSummaries = items.stream()
            .map(CartService::toCartItemSummary)
            .collect(Collectors.toList());

        BigDecimal totalAmount = itemSummaries.stream()
            .map(CartItemSummary::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalQuantity = itemSummaries.stream()
            .mapToInt(CartItemSummary::getQuantity)
            .sum();

        return new CartSummaryResponse(itemSummaries, totalAmount, totalQuantity);
    }

    @Transactional
    public CartItem addToCart(Integer userId, Integer productId, Integer quantity) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        if (quantity == null || quantity < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
        }

        User user = requireUser(userId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Integer stockQuantity = product.getStockQuantity();
        if (stockQuantity == null || stockQuantity < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock");
        }

        Cart cart = cartRepository.findByUserAndStatus(user, Cart.CartStatus.ACTIVE)
            .orElseGet(() -> cartRepository.save(new Cart(user)));

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        CartItem cartItem;

        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            int updatedQuantity = cartItem.getQuantity() + quantity;

            if (updatedQuantity > stockQuantity) {
                throw new IllegalArgumentException("Insufficient stock");
            }

            cartItem.setQuantity(updatedQuantity);
            cartItem.setPriceAtTime(product.getPrice());
        } else {
            cartItem = new CartItem(cart, product, quantity);
            cart.addItem(cartItem);
        }

        cart.recalculateTotal();
        cartRepository.save(cart);

        return cartItem;
    }

    @Transactional
    public CartItem updateCartItemQuantity(Integer cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        if (quantity == null || quantity < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
        }

        Product product = cartItem.getProduct();
        Integer stockQuantity = product != null ? product.getStockQuantity() : null;
        if (stockQuantity == null || quantity > stockQuantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock");
        }

        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public CartSummaryResponse removeCartItem(Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        cartItemRepository.flush();

        return buildCartSummary(cart);
    }

    @Transactional
    public CartSummaryResponse clearCart(Integer userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        User user = requireUser(userId);

        Cart cart = cartRepository.findByUserAndStatus(user, Cart.CartStatus.ACTIVE)
            .orElse(null);

        if (cart == null || cart.getCartId() == null) {
            return new CartSummaryResponse(List.of(), BigDecimal.ZERO, 0);
        }

        cartItemRepository.deleteByCartId(cart.getCartId());
        cartItemRepository.flush();

        return new CartSummaryResponse(List.of(), BigDecimal.ZERO, 0);
    }

    @Transactional
    public CartSummaryResponse checkoutCart(Integer userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        User user = requireUser(userId);

        Cart cart = cartRepository.findByUserAndStatus(user, Cart.CartStatus.ACTIVE)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        CartSummaryResponse summary = buildCartSummary(cart);
        if (summary.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        cartItemRepository.deleteByCartId(cart.getCartId());
        cartItemRepository.flush();
        cart.setStatus(Cart.CartStatus.COMPLETED);
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);

        return summary;
    }

    @Transactional
    public Cart updateCart(Integer cartId, Cart cartDetails) {
        Cart existingCart = getCartById(cartId);

        existingCart.setUser(cartDetails.getUser());
        existingCart.setProduct(cartDetails.getProduct());
        existingCart.setQuantity(cartDetails.getQuantity());
        existingCart.setAddedAt(cartDetails.getAddedAt() != null ? cartDetails.getAddedAt() : existingCart.getAddedAt());
        existingCart.setTotalAmount(cartDetails.getTotalAmount());
        existingCart.setStatus(cartDetails.getStatus());

        return cartRepository.save(existingCart);
    }

    public void deleteCart(Integer cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        }

        cartRepository.deleteById(cartId);
    }

    private static CartItemSummary toCartItemSummary(CartItem item) {
        Product product = item.getProduct();
        BigDecimal unitPrice = item.getPriceAtTime() != null
            ? item.getPriceAtTime()
            : (product != null ? product.getPrice() : BigDecimal.ZERO);
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity() == null ? 0 : item.getQuantity()));

        return new CartItemSummary(
            item.getCartItemId(),
            item.getQuantity(),
            unitPrice,
            subtotal,
            product == null ? null : new ProductSummary(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.getStockQuantity()
            )
        );
    }

    private User requireUser(Integer userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private CartSummaryResponse buildCartSummary(Cart cart) {
        if (cart == null || cart.getCartId() == null) {
            return new CartSummaryResponse(List.of(), BigDecimal.ZERO, 0);
        }

        List<CartItem> items = cartItemRepository.findByCart_CartId(cart.getCartId());
        List<CartItemSummary> itemSummaries = items.stream()
            .map(CartService::toCartItemSummary)
            .collect(Collectors.toList());

        BigDecimal totalAmount = itemSummaries.stream()
            .map(CartItemSummary::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalQuantity = itemSummaries.stream()
            .mapToInt(CartItemSummary::getQuantity)
            .sum();

        return new CartSummaryResponse(itemSummaries, totalAmount, totalQuantity);
    }

    public static class CartSummaryResponse {
        private final List<CartItemSummary> items;
        private final BigDecimal totalAmount;
        private final int totalQuantity;

        public CartSummaryResponse(List<CartItemSummary> items, BigDecimal totalAmount, int totalQuantity) {
            this.items = items;
            this.totalAmount = totalAmount;
            this.totalQuantity = totalQuantity;
        }

        public List<CartItemSummary> getItems() {
            return items;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }
    }

    public static class CartItemSummary {
        private final Integer cartItemId;
        private final Integer quantity;
        private final BigDecimal priceAtTime;
        private final BigDecimal subtotal;
        private final ProductSummary product;

        public CartItemSummary(Integer cartItemId, Integer quantity, BigDecimal priceAtTime, BigDecimal subtotal, ProductSummary product) {
            this.cartItemId = cartItemId;
            this.quantity = quantity;
            this.priceAtTime = priceAtTime;
            this.subtotal = subtotal;
            this.product = product;
        }

        public Integer getCartItemId() {
            return cartItemId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public BigDecimal getPriceAtTime() {
            return priceAtTime;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public ProductSummary getProduct() {
            return product;
        }
    }

    public static class ProductSummary {
        private final Integer productId;
        private final String name;
        private final String description;
        private final BigDecimal price;
        private final String category;
        private final String imageUrl;
        private final Integer stockQuantity;

        public ProductSummary(
            Integer productId,
            String name,
            String description,
            BigDecimal price,
            String category,
            String imageUrl,
            Integer stockQuantity
        ) {
            this.productId = productId;
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.imageUrl = imageUrl;
            this.stockQuantity = stockQuantity;
        }

        public Integer getProductId() {
            return productId;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public String getCategory() {
            return category;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }
    }
}

