package tests;

import base.BaseTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.ProductListingPage;
import pages.ProductListingPage.ProductSnapshot;
import pages.RegistrationPage;
import utils.LoginUtils;

public class CartTest extends BaseTest {
    private static final String PASSWORD = "Test@1234";

    private String uniqueEmail() {
        return "cartuser" + System.currentTimeMillis() + "@example.com";
    }

    private String uniquePhoneNumber() {
        String suffix = String.valueOf(System.currentTimeMillis());
        return suffix.substring(Math.max(0, suffix.length() - 10));
    }

    private String seedRegisteredUser() {
        String email = uniqueEmail();
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.register("Cart User", uniquePhoneNumber(), email, PASSWORD, PASSWORD);
        Assert.assertTrue(
                registrationPage.getFeedbackMessage().contains("Account created"),
                "User registration should succeed before login."
        );
        return email;
    }

    private void pauseAfterAction() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private BigDecimal parseMoney(String value) {
        String sanitized = value == null ? "" : value.replaceAll("[^0-9.]", "");
        if (sanitized.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(sanitized);
    }

    private void assertMoneyEquals(BigDecimal actual, BigDecimal expected, String message) {
        Assert.assertTrue(actual.subtract(expected).abs().compareTo(new BigDecimal("0.01")) <= 0, message);
    }

    private BigDecimal priceOf(ProductSnapshot product) {
        return parseMoney(product.getPrice());
    }

    private BigDecimal subtotalOf(ProductSnapshot product, int quantity) {
        return priceOf(product).multiply(BigDecimal.valueOf(quantity));
    }

    private void assertProductState(CartPage cartPage, ProductSnapshot product, int expectedQuantity) {
        Assert.assertTrue(cartPage.isProductVisible(product.getName()), "Cart should contain product: " + product.getName());
        Assert.assertEquals(cartPage.getProductName(product.getName()), product.getName(), "Product name should match the listing data.");
        assertMoneyEquals(cartPage.getProductPriceAmount(product.getName()), priceOf(product), "Product price should match the listing data for " + product.getName());
        Assert.assertEquals(cartPage.getProductQuantity(product.getName()), expectedQuantity, "Product quantity should match the expected value for " + product.getName());
        assertMoneyEquals(
                cartPage.getProductSubtotalAmount(product.getName()),
                subtotalOf(product, expectedQuantity),
                "Product subtotal should match the expected quantity for " + product.getName()
        );
    }

    @Test(description = "T090: Validate Cart Items")
    public void validateCartItems() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        List<ProductSnapshot> selectedProducts = new ArrayList<>();
        int firstIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot firstProduct = productListingPage.getProductSnapshot(firstIndex);
        selectedProducts.add(firstProduct);
        productListingPage.addProductToCart(firstIndex);

        int secondIndex = -1;
        try {
            secondIndex = productListingPage.findNextAddableProductIndex(firstIndex);
        } catch (IllegalStateException ignored) {
            secondIndex = -1;
        }

        if (secondIndex >= 0) {
            ProductSnapshot secondProduct = productListingPage.getProductSnapshot(secondIndex);
            selectedProducts.add(secondProduct);
            productListingPage.addProductToCart(secondIndex);
        }

        CartPage cartPage = new CartPage(driver).open();
        pauseAfterAction();
        cartPage.waitUntilCartItemCount(selectedProducts.size());
        cartPage.waitUntilCartTotalQuantity(selectedProducts.size());

        Assert.assertFalse(cartPage.isCartEmpty(), "Cart should not be empty after adding products.");
        Assert.assertEquals(cartPage.getCartItemCount(), selectedProducts.size(), "Cart item count should match the number of added products.");
        Assert.assertEquals(cartPage.getCartTotalQuantity(), selectedProducts.size(), "Cart total quantity should match the number of added products.");

        BigDecimal expectedSubtotal = BigDecimal.ZERO;
        for (ProductSnapshot product : selectedProducts) {
            expectedSubtotal = expectedSubtotal.add(parseMoney(product.getPrice()));
            Assert.assertTrue(cartPage.isProductVisible(product.getName()), "Cart should contain product: " + product.getName());
            Assert.assertEquals(cartPage.getProductName(product.getName()), product.getName());
            assertMoneyEquals(cartPage.getProductPriceAmount(product.getName()), parseMoney(product.getPrice()), "Product price should match the selected product price for " + product.getName());
            Assert.assertEquals(cartPage.getProductQuantity(product.getName()), 1, "Default quantity should be 1 for " + product.getName());
            assertMoneyEquals(cartPage.getProductSubtotalAmount(product.getName()), parseMoney(product.getPrice()), "Product subtotal should match the selected product price for " + product.getName());
        }

        assertMoneyEquals(cartPage.getCartSubtotalAmount(), expectedSubtotal, "Cart subtotal should match the sum of all added products.");
    }

    @Test(description = "T091: Verify Quantity & Price")
    public void verifyQuantityAndPrice() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        int productIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot product = productListingPage.getProductSnapshot(productIndex);
        productListingPage.addProductToCart(productIndex);

        CartPage cartPage = new CartPage(driver).open();
        pauseAfterAction();
        cartPage.waitUntilCartItemCount(1);
        cartPage.waitUntilCartTotalQuantity(1);
        cartPage.waitUntilProductQuantity(product.getName(), 1);
        cartPage.waitUntilProductSubtotal(product.getName(), parseMoney(product.getPrice()));
        cartPage.waitUntilCartSubtotal(parseMoney(product.getPrice()));

        Assert.assertFalse(cartPage.isCartEmpty(), "Cart should not be empty after adding a product.");
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart should contain one item.");
        Assert.assertEquals(cartPage.getCartTotalQuantity(), 1, "Default cart quantity should be 1.");
        Assert.assertEquals(cartPage.getProductQuantity(product.getName()), 1, "Default quantity should be displayed as 1.");
        Assert.assertEquals(cartPage.getProductName(product.getName()), product.getName());
        assertMoneyEquals(cartPage.getProductPriceAmount(product.getName()), parseMoney(product.getPrice()), "Displayed price should match the selected product price.");
        assertMoneyEquals(cartPage.getProductSubtotalAmount(product.getName()), parseMoney(product.getPrice()), "Default subtotal should match the selected product price.");
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), parseMoney(product.getPrice()), "Cart subtotal should match the selected product price.");

        cartPage.increaseQuantity(product.getName());
        pauseAfterAction();
        cartPage.waitUntilProductQuantity(product.getName(), 2);
        cartPage.waitUntilProductSubtotal(product.getName(), parseMoney(product.getPrice()).multiply(BigDecimal.valueOf(2)));
        cartPage.waitUntilCartTotalQuantity(2);
        cartPage.waitUntilCartSubtotal(parseMoney(product.getPrice()).multiply(BigDecimal.valueOf(2)));

        Assert.assertEquals(cartPage.getProductQuantity(product.getName()), 2, "Quantity should increase to 2.");
        assertMoneyEquals(cartPage.getProductSubtotalAmount(product.getName()), parseMoney(product.getPrice()).multiply(BigDecimal.valueOf(2)), "Subtotal should update when quantity increases.");
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), parseMoney(product.getPrice()).multiply(BigDecimal.valueOf(2)), "Cart subtotal should update when quantity increases.");

        cartPage.decreaseQuantity(product.getName());
        pauseAfterAction();
        cartPage.waitUntilProductQuantity(product.getName(), 1);
        cartPage.waitUntilProductSubtotal(product.getName(), parseMoney(product.getPrice()));
        cartPage.waitUntilCartTotalQuantity(1);
        cartPage.waitUntilCartSubtotal(parseMoney(product.getPrice()));

        Assert.assertEquals(cartPage.getProductQuantity(product.getName()), 1, "Quantity should decrease back to 1.");
        assertMoneyEquals(cartPage.getProductSubtotalAmount(product.getName()), parseMoney(product.getPrice()), "Subtotal should recalculate after decreasing quantity.");
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), parseMoney(product.getPrice()), "Cart subtotal should recalculate after decreasing quantity.");
    }

    @Test(description = "T092: Automate Update Cart")
    public void automateUpdateCart() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        int productIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot product = productListingPage.getProductSnapshot(productIndex);
        productListingPage.addProductToCart(productIndex);
        pauseAfterAction();

        CartPage cartPage = new CartPage(driver).open();
        pauseAfterAction();
        cartPage.waitUntilLoaded();
        pauseAfterAction();
        cartPage.waitUntilCartItemCount(1);
        cartPage.waitUntilCartTotalQuantity(1);
        cartPage.waitUntilProductQuantity(product.getName(), 1);
        cartPage.waitUntilProductSubtotal(product.getName(), priceOf(product));
        cartPage.waitUntilCartSubtotal(priceOf(product));

        assertProductState(cartPage, product, 1);
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), priceOf(product), "Cart subtotal should match the initial product price.");

        cartPage.increaseQuantity(product.getName());
        pauseAfterAction();
        cartPage.waitUntilProductQuantity(product.getName(), 2);
        cartPage.waitUntilProductSubtotal(product.getName(), subtotalOf(product, 2));
        cartPage.waitUntilCartTotalQuantity(2);
        cartPage.waitUntilCartSubtotal(subtotalOf(product, 2));

        Assert.assertEquals(cartPage.getProductQuantity(product.getName()), 2, "Quantity should update to 2 after increase.");
        assertMoneyEquals(cartPage.getProductSubtotalAmount(product.getName()), subtotalOf(product, 2), "Subtotal should update correctly after increasing quantity.");
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), subtotalOf(product, 2), "Cart subtotal should update correctly after increasing quantity.");
        Assert.assertTrue(cartPage.getActionSuccessMessage().contains("Cart updated successfully"), "Cart update success message should be displayed.");

        cartPage.decreaseQuantity(product.getName());
        pauseAfterAction();
        cartPage.waitUntilProductQuantity(product.getName(), 1);
        cartPage.waitUntilProductSubtotal(product.getName(), priceOf(product));
        cartPage.waitUntilCartTotalQuantity(1);
        cartPage.waitUntilCartSubtotal(priceOf(product));

        Assert.assertEquals(cartPage.getProductQuantity(product.getName()), 1, "Quantity should update back to 1 after decrease.");
        assertMoneyEquals(cartPage.getProductSubtotalAmount(product.getName()), priceOf(product), "Subtotal should recalculate correctly after decreasing quantity.");
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), priceOf(product), "Cart subtotal should recalculate correctly after decreasing quantity.");
    }

    @Test(description = "T093: Automate Remove Item")
    public void automateRemoveItem() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        int firstIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot firstProduct = productListingPage.getProductSnapshot(firstIndex);
        productListingPage.addProductToCart(firstIndex);
        pauseAfterAction();

        int secondIndex = productListingPage.findNextAddableProductIndex(firstIndex);
        ProductSnapshot secondProduct = productListingPage.getProductSnapshot(secondIndex);
        productListingPage.addProductToCart(secondIndex);
        pauseAfterAction();

        CartPage cartPage = new CartPage(driver).open();
        pauseAfterAction();
        cartPage.waitUntilLoaded();
        pauseAfterAction();
        cartPage.waitUntilCartItemCount(2);
        cartPage.waitUntilCartTotalQuantity(2);

        cartPage.removeItem(firstProduct.getName());
        pauseAfterAction();
        cartPage.waitUntilProductNotVisible(firstProduct.getName());
        cartPage.waitUntilCartItemCount(1);
        cartPage.waitUntilCartTotalQuantity(1);

        Assert.assertFalse(cartPage.isProductVisible(firstProduct.getName()), "Removed product should no longer be displayed in the cart.");
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart item count should decrease after removing a product.");
        Assert.assertEquals(cartPage.getCartTotalQuantity(), 1, "Total quantity should decrease after removing a product.");
        assertProductState(cartPage, secondProduct, 1);
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), priceOf(secondProduct), "Cart subtotal should update after removing a product.");
        Assert.assertTrue(cartPage.getActionSuccessMessage().contains("Cart item removed successfully"), "Remove success message should be displayed.");
    }

    @Test(description = "T094: Validate Cart Summary")
    public void validateCartSummary() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        int firstIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot firstProduct = productListingPage.getProductSnapshot(firstIndex);
        productListingPage.addProductToCart(firstIndex);
        pauseAfterAction();

        int secondIndex = productListingPage.findNextAddableProductIndex(firstIndex);
        ProductSnapshot secondProduct = productListingPage.getProductSnapshot(secondIndex);
        productListingPage.addProductToCart(secondIndex);
        pauseAfterAction();

        CartPage cartPage = new CartPage(driver).open();
        pauseAfterAction();
        cartPage.waitUntilLoaded();
        pauseAfterAction();
        cartPage.waitUntilCartItemCount(2);
        cartPage.waitUntilCartTotalQuantity(2);

        assertProductState(cartPage, firstProduct, 1);
        assertProductState(cartPage, secondProduct, 1);

        BigDecimal expectedSubtotal = priceOf(firstProduct).add(priceOf(secondProduct));
        Assert.assertEquals(cartPage.getCartTotalQuantity(), 2, "Cart total quantity should equal the number of items added.");
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), expectedSubtotal, "Cart subtotal should equal the sum of product subtotals.");
        assertMoneyEquals(cartPage.getProductPriceAmount(firstProduct.getName()), priceOf(firstProduct), "First product price should be displayed correctly.");
        assertMoneyEquals(cartPage.getProductPriceAmount(secondProduct.getName()), priceOf(secondProduct), "Second product price should be displayed correctly.");
        assertMoneyEquals(cartPage.getProductSubtotalAmount(firstProduct.getName()), priceOf(firstProduct), "First product subtotal should be displayed correctly.");
        assertMoneyEquals(cartPage.getProductSubtotalAmount(secondProduct.getName()), priceOf(secondProduct), "Second product subtotal should be displayed correctly.");
        Assert.assertFalse(cartPage.getCartSubtotal().isBlank(), "Displayed subtotal should be readable from the summary.");
    }

    @Test(description = "T095: Handle Edge Cases")
    public void handleCartEdgeCases() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);
        pauseAfterAction();

        CartPage emptyCartPage = new CartPage(driver).open();
        pauseAfterAction();
        emptyCartPage.waitUntilLoaded();
        pauseAfterAction();

        Assert.assertTrue(emptyCartPage.isCartEmpty(), "Newly created cart should start empty.");
        emptyCartPage.waitUntilEmptyCartMessage();
        Assert.assertTrue(emptyCartPage.isEmptyCartMessageVisible(), "Empty cart message should be displayed.");
        Assert.assertEquals(emptyCartPage.getEmptyCartMessage(), "Your cart is empty.", "Empty cart validation message should be correct.");

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        int productIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot product = productListingPage.getProductSnapshot(productIndex);
        productListingPage.addProductToCart(productIndex);
        pauseAfterAction();

        CartPage cartPage = new CartPage(driver).open();
        pauseAfterAction();
        cartPage.waitUntilLoaded();
        pauseAfterAction();
        cartPage.waitUntilCartItemCount(1);
        cartPage.waitUntilCartTotalQuantity(1);
        assertProductState(cartPage, product, 1);

        Assert.assertTrue(cartPage.isDecreaseQuantityDisabled(product.getName()), "Quantity should not go below the minimum allowed value.");

        cartPage.increaseQuantity(product.getName());
        pauseAfterAction();
        cartPage.increaseQuantity(product.getName());
        pauseAfterAction();
        cartPage.increaseQuantity(product.getName());
        pauseAfterAction();
        cartPage.increaseQuantity(product.getName());
        pauseAfterAction();
        cartPage.waitUntilProductQuantity(product.getName(), 5);
        cartPage.waitUntilProductSubtotal(product.getName(), subtotalOf(product, 5));
        cartPage.waitUntilCartTotalQuantity(5);
        cartPage.waitUntilCartSubtotal(subtotalOf(product, 5));

        Assert.assertEquals(cartPage.getProductQuantity(product.getName()), 5, "Large quantity updates should be handled correctly.");
        assertMoneyEquals(cartPage.getProductSubtotalAmount(product.getName()), subtotalOf(product, 5), "Subtotal should update correctly for large quantities.");
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), subtotalOf(product, 5), "Cart subtotal should update correctly for large quantities.");

        cartPage.refresh();
        pauseAfterAction();
        cartPage.waitUntilLoaded();
        pauseAfterAction();
        cartPage.waitUntilProductQuantity(product.getName(), 5);
        cartPage.waitUntilProductSubtotal(product.getName(), subtotalOf(product, 5));
        cartPage.waitUntilCartTotalQuantity(5);
        cartPage.waitUntilCartSubtotal(subtotalOf(product, 5));

        Assert.assertEquals(cartPage.getProductQuantity(product.getName()), 5, "Cart should remain stable after page refresh.");
        assertMoneyEquals(cartPage.getCartSubtotalAmount(), subtotalOf(product, 5), "Cart subtotal should remain stable after page refresh.");

        cartPage.removeItem(product.getName());
        pauseAfterAction();
        cartPage.waitUntilCartItemCount(0);
        cartPage.waitUntilEmptyCartMessage();

        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty after removing all products.");
        Assert.assertTrue(cartPage.isEmptyCartMessageVisible(), "Empty cart message should appear after removing all products.");
    }
}
