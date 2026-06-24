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

    private void assertCheckoutSummaryMatchesProducts(CheckoutPage checkoutPage, List<ProductSnapshot> selectedProducts) {
        Assert.assertTrue(checkoutPage.isOrderSummaryDisplayed(), "Order summary section should be displayed.");
        Assert.assertEquals(
                checkoutPage.getSelectedProductCount(),
                selectedProducts.size(),
                "Selected products should appear in checkout summary."
        );
        Assert.assertEquals(
                checkoutPage.getCheckoutSummaryTotalQuantity(),
                selectedProducts.size(),
                "Checkout summary quantity should match the number of selected products."
        );

        BigDecimal expectedTotal = BigDecimal.ZERO;
        for (ProductSnapshot product : selectedProducts) {
            BigDecimal productPrice = parseMoney(product.getPrice());
            expectedTotal = expectedTotal.add(productPrice);

            Assert.assertTrue(
                    checkoutPage.isSelectedProductVisible(product.getName()),
                    "Checkout summary should include product: " + product.getName()
            );

            String summaryDetails = checkoutPage.getCheckoutSummaryItemDetails(product.getName());
            Assert.assertTrue(
                    summaryDetails.contains(product.getName()),
                    "Checkout summary should show the product name for " + product.getName()
            );
            Assert.assertTrue(
                    summaryDetails.contains("1 x"),
                    "Checkout summary should show the quantity for " + product.getName()
            );
        }

        assertMoneyEquals(
                checkoutPage.getCheckoutSummaryTotalAmount(),
                expectedTotal,
                "Checkout summary total should equal the sum of selected products."
        );
    }

    private void assertValidCheckoutValuesAreAccepted(CheckoutPage checkoutPage) {
        Assert.assertEquals(checkoutPage.getFullNameValue(), "Checkout User", "Full name should accept valid data.");
        Assert.assertEquals(checkoutPage.getPhoneNumberValue(), "9876543210", "Phone number should accept valid data.");
        Assert.assertEquals(checkoutPage.getAddressValue(), "221 Baker Street", "Address should accept valid data.");
        Assert.assertEquals(checkoutPage.getCityValue(), "London", "City should accept valid data.");
        Assert.assertEquals(checkoutPage.getStateValue(), "Greater London", "State should accept valid data.");
        Assert.assertEquals(checkoutPage.getPincodeValue(), "560001", "Pincode should accept valid data.");
        Assert.assertEquals(checkoutPage.getPaymentMethodValue(), "COD", "Payment method should remain selected.");
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
        Assert.assertTrue(checkoutPage.areMandatoryFieldsDisplayed(), "Checkout form fields should be visible.");
        assertCheckoutSummaryMatchesProducts(checkoutPage, selectedProducts);
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

    @Test(description = "T098: Test Positive Checkout Flow")
    public void testPositiveCheckoutFlow() {
        List<ProductSnapshot> selectedProducts = new ArrayList<>();
        CheckoutPage checkoutPage = prepareCheckoutScenario(selectedProducts);

        Assert.assertTrue(checkoutPage.isOnCheckoutPage(), "User should land on the checkout page.");
        Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should be fully loaded.");
        Assert.assertTrue(checkoutPage.areMandatoryFieldsDisplayed(), "All mandatory checkout fields should be displayed.");
        assertCheckoutSummaryMatchesProducts(checkoutPage, selectedProducts);

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
        assertValidCheckoutValuesAreAccepted(checkoutPage);

        checkoutPage.submitCheckout();
        pauseAfterAction();
        checkoutPage.waitUntilOrderSuccessRedirect();
        checkoutPage.waitUntilOrderSuccessDisplayed();
        pauseAfterAction();

        Assert.assertTrue(
                checkoutPage.getCurrentUrl().contains("/order-success"),
                "User should proceed to the next checkout step successfully."
        );
        Assert.assertTrue(
                checkoutPage.isOrderSuccessDisplayed(),
                "Order success page should be displayed after checkout completes."
        );
    }

    @Test(description = "T099: Test Negative Scenarios")
    public void testNegativeCheckoutScenarios() {
        List<ProductSnapshot> selectedProducts = new ArrayList<>();
        CheckoutPage checkoutPage = prepareCheckoutScenario(selectedProducts);

        Assert.assertTrue(checkoutPage.isOnCheckoutPage(), "User should remain on the checkout page.");
        Assert.assertTrue(checkoutPage.areMandatoryFieldsDisplayed(), "Checkout form should be available for validation.");
        assertCheckoutSummaryMatchesProducts(checkoutPage, selectedProducts);

        checkoutPage.submitCheckout();
        pauseAfterAction();
        checkoutPage.waitUntilRequiredFieldErrorsVisible();
        checkoutPage.waitUntilFormErrorVisible();
        pauseAfterAction();

        Assert.assertEquals(checkoutPage.getNameErrorMessage(), "Name is required", "Name required validation should appear.");
        Assert.assertEquals(checkoutPage.getPhoneErrorMessage(), "Phone is required", "Phone required validation should appear.");
        Assert.assertEquals(checkoutPage.getAddressErrorMessage(), "Address is required", "Address required validation should appear.");
        Assert.assertEquals(checkoutPage.getCityErrorMessage(), "City is required", "City required validation should appear.");
        Assert.assertEquals(checkoutPage.getStateErrorMessage(), "State is required", "State required validation should appear.");
        Assert.assertEquals(checkoutPage.getPincodeErrorMessage(), "Pincode is required", "Pincode required validation should appear.");
        Assert.assertTrue(
                checkoutPage.getFormErrorMessage().contains("Please fix the highlighted fields"),
                "A form-level validation message should appear for empty submission."
        );

        checkoutPage.fillCheckoutForm(
                " ",
                "12345",
                " ",
                "London",
                "Greater London",
                "12",
                "COD"
        );
        pauseAfterAction();
        checkoutPage.submitCheckout();
        pauseAfterAction();
        checkoutPage.waitUntilNameAddressPhonePincodeErrorsVisible();
        checkoutPage.waitUntilFormErrorVisible();
        pauseAfterAction();

        Assert.assertEquals(checkoutPage.getNameErrorMessage(), "Name is required", "Whitespace name should be rejected.");
        Assert.assertEquals(checkoutPage.getPhoneErrorMessage(), "Phone must be 10 digits", "Invalid phone validation should appear.");
        Assert.assertEquals(checkoutPage.getAddressErrorMessage(), "Address is required", "Whitespace address should be rejected.");
        Assert.assertEquals(checkoutPage.getPincodeErrorMessage(), "Pincode must be 6 digits", "Invalid pincode validation should appear.");
        Assert.assertTrue(
                checkoutPage.getFormErrorMessage().contains("Please fix the highlighted fields"),
                "A form-level validation message should remain visible for invalid inputs."
        );
        Assert.assertTrue(checkoutPage.isOnCheckoutPage(), "User should not leave the checkout page with invalid inputs.");
        Assert.assertFalse(checkoutPage.isOrderSuccessDisplayed(), "User must not reach the next checkout step with invalid inputs.");
    }
}
