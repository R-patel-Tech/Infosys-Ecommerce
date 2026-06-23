package tests;

import base.BaseTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.ProductListingPage;
import pages.ProductListingPage.ProductSnapshot;
import pages.RegistrationPage;
import utils.LoginUtils;

public class CheckoutTest extends BaseTest {
    private static final String PASSWORD = "Test@1234";

    private String uniqueEmail() {
        return "checkoutuser" + System.currentTimeMillis() + "@example.com";
    }

    private String uniquePhoneNumber() {
        String suffix = String.valueOf(System.currentTimeMillis());
        return suffix.substring(Math.max(0, suffix.length() - 10));
    }

    private String seedRegisteredUser() {
        String email = uniqueEmail();
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.register("Checkout User", uniquePhoneNumber(), email, PASSWORD, PASSWORD);
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

    private void assertMoneyEquals(BigDecimal actual, BigDecimal expected, String message) {
        Assert.assertTrue(actual.subtract(expected).abs().compareTo(new BigDecimal("0.01")) <= 0, message);
    }

    private List<ProductSnapshot> addProductsToCart() {
        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        List<ProductSnapshot> selectedProducts = new ArrayList<>();
        int firstIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot firstProduct = productListingPage.getProductSnapshot(firstIndex);
        selectedProducts.add(firstProduct);
        productListingPage.addProductToCart(firstIndex);
        pauseAfterAction();

        try {
            int secondIndex = productListingPage.findNextAddableProductIndex(firstIndex);
            ProductSnapshot secondProduct = productListingPage.getProductSnapshot(secondIndex);
            selectedProducts.add(secondProduct);
            productListingPage.addProductToCart(secondIndex);
            pauseAfterAction();
        } catch (IllegalStateException ignored) {
            // One product is enough for the checkout initiation flow.
        }

        return selectedProducts;
    }

    private CheckoutPage openCheckoutPage() {
        CartPage cartPage = new CartPage(driver).open();
        pauseAfterAction();
        CheckoutPage checkoutPage = cartPage.clickCheckout();
        pauseAfterAction();
        checkoutPage.waitUntilLoaded();
        pauseAfterAction();
        return checkoutPage;
    }

    private CheckoutPage prepareCheckoutScenario(List<ProductSnapshot> selectedProducts) {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);
        pauseAfterAction();
        selectedProducts.addAll(addProductsToCart());
        return openCheckoutPage();
    }

    @Test(description = "T096: Automate Checkout Initiation")
    public void automateCheckoutInitiation() {
        List<ProductSnapshot> selectedProducts = new ArrayList<>();
        CheckoutPage checkoutPage = prepareCheckoutScenario(selectedProducts);

        Assert.assertTrue(checkoutPage.isOnCheckoutPage(), "User should be redirected to the checkout page.");
        Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should load successfully.");
        Assert.assertTrue(checkoutPage.isOrderSummaryDisplayed(), "Order summary section should be displayed.");
        Assert.assertTrue(checkoutPage.areMandatoryFieldsDisplayed(), "Checkout form fields should be visible.");
        Assert.assertEquals(checkoutPage.getSelectedProductCount(), selectedProducts.size(), "Selected products should appear in checkout summary.");

        for (ProductSnapshot product : selectedProducts) {
            Assert.assertTrue(checkoutPage.isSelectedProductVisible(product.getName()), "Checkout summary should include product: " + product.getName());
        }
    }

    @Test(description = "T097: Validate Form Inputs")
    public void validateFormInputs() {
        List<ProductSnapshot> selectedProducts = new ArrayList<>();
        CheckoutPage checkoutPage = prepareCheckoutScenario(selectedProducts);

        Assert.assertTrue(checkoutPage.areMandatoryFieldsDisplayed(), "All mandatory checkout fields should be displayed.");
        Assert.assertTrue(checkoutPage.isOrderSummaryDisplayed(), "Order summary should remain visible while validating the form.");

        checkoutPage.submitCheckout();
        pauseAfterAction();
        checkoutPage.waitUntilRequiredFieldErrorsVisible();
        pauseAfterAction();

        Assert.assertEquals(checkoutPage.getNameErrorMessage(), "Name is required", "Name is required validation should appear.");
        Assert.assertEquals(checkoutPage.getPhoneErrorMessage(), "Phone is required", "Phone is required validation should appear.");
        Assert.assertEquals(checkoutPage.getAddressErrorMessage(), "Address is required", "Address is required validation should appear.");
        Assert.assertEquals(checkoutPage.getCityErrorMessage(), "City is required", "City is required validation should appear.");
        Assert.assertEquals(checkoutPage.getStateErrorMessage(), "State is required", "State is required validation should appear.");
        Assert.assertEquals(checkoutPage.getPincodeErrorMessage(), "Pincode is required", "Pincode is required validation should appear.");
        Assert.assertTrue(
                checkoutPage.getFormErrorMessage().contains("Please fix the highlighted fields"),
                "A form-level validation message should be displayed for empty submission."
        );

        checkoutPage.fillCheckoutForm(
                "Checkout User",
                "12345",
                "221 Baker Street",
                "London",
                "Greater London",
                "12",
                "COD"
        );
        pauseAfterAction();
        checkoutPage.submitCheckout();
        pauseAfterAction();
        checkoutPage.waitUntilInvalidInputErrorsVisible();
        pauseAfterAction();

        Assert.assertEquals(checkoutPage.getPhoneErrorMessage(), "Phone must be 10 digits", "Invalid phone validation should appear.");
        Assert.assertEquals(checkoutPage.getPincodeErrorMessage(), "Pincode must be 6 digits", "Invalid pincode validation should appear.");

        checkoutPage.fillCheckoutForm(
                "Checkout User",
                "9876543210",
                "221 Baker Street",
                "London",
                "Greater London",
                "560001",
                "COD"
        );
        pauseAfterAction();
        checkoutPage.submitCheckout();
        pauseAfterAction();
        checkoutPage.waitUntilOrderSuccessRedirect();
        pauseAfterAction();

        Assert.assertTrue(checkoutPage.getCurrentUrl().contains("/order-success"), "User should proceed to the next checkout step after valid input.");
    }
}