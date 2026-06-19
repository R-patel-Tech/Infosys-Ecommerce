package tests;

import base.BaseTest;
import java.math.BigDecimal;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.ProductListingPage;
import pages.ProductListingPage.ProductSnapshot;
import pages.RegistrationPage;
import utils.LoginUtils;

public class AddToCartTest extends BaseTest {
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
            Thread.sleep(2000);
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

    @Test
    public void automateAddToCartFunctionality() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        int firstIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot firstProduct = productListingPage.getProductSnapshot(firstIndex);
        productListingPage.addProductToCart(firstIndex);

        CartPage cartPage = new CartPage(driver).open();
        pauseAfterAction();
        Assert.assertEquals(cartPage.getCartTotalQuantity(), 1, "Cart count should increase after the first add.");
        Assert.assertTrue(cartPage.isProductVisible(firstProduct.getName()), "First product should be visible in cart.");

        productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        int secondIndex = productListingPage.findNextAddableProductIndex(firstIndex);
        ProductSnapshot secondProduct = productListingPage.getProductSnapshot(secondIndex);
        productListingPage.addProductToCart(secondIndex);

        cartPage = new CartPage(driver).open();
        pauseAfterAction();
        Assert.assertEquals(cartPage.getCartTotalQuantity(), 2, "Cart count should increase after multiple adds.");
        Assert.assertTrue(cartPage.isProductVisible(firstProduct.getName()), "First product should remain in cart.");
        Assert.assertTrue(cartPage.isProductVisible(secondProduct.getName()), "Second product should be visible in cart.");
    }

    @Test
    public void validateProductAddition() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        int productIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot selectedProduct = productListingPage.getProductSnapshot(productIndex);
        productListingPage.addProductToCart(productIndex);

        CartPage cartPage = new CartPage(driver).open();
        pauseAfterAction();
        Assert.assertTrue(cartPage.isProductVisible(selectedProduct.getName()), "Product should be visible in the cart.");
        Assert.assertEquals(cartPage.getProductName(selectedProduct.getName()), selectedProduct.getName());
        Assert.assertEquals(parseMoney(cartPage.getProductPrice(selectedProduct.getName())), parseMoney(selectedProduct.getPrice()));
        Assert.assertEquals(cartPage.getProductQuantity(selectedProduct.getName()), 1);
        Assert.assertEquals(
                parseMoney(cartPage.getProductSubtotal(selectedProduct.getName())),
                parseMoney(selectedProduct.getPrice())
        );
        Assert.assertEquals(
                cartPage.getCartSubtotalAmount(),
                parseMoney(selectedProduct.getPrice()),
                "Cart subtotal should be calculated correctly."
        );

        cartPage.refresh();
        pauseAfterAction();

        Assert.assertTrue(cartPage.isProductVisible(selectedProduct.getName()), "Product should remain in cart after refresh.");
        Assert.assertEquals(cartPage.getProductName(selectedProduct.getName()), selectedProduct.getName());
        Assert.assertEquals(cartPage.getProductQuantity(selectedProduct.getName()), 1);
        Assert.assertEquals(
                cartPage.getCartSubtotalAmount(),
                parseMoney(selectedProduct.getPrice()),
                "Cart subtotal should remain correct after refresh."
        );
    }
}
