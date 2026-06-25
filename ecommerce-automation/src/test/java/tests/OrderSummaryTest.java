package tests;

import base.BaseTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.OrderSummaryPage;
import pages.ProductListingPage;
import pages.ProductListingPage.ProductSnapshot;
import pages.RegistrationPage;
import utils.LoginUtils;

public class OrderSummaryTest extends BaseTest {
    private static final String PASSWORD = "Test@1234";

    private String uniqueEmail() {
        return "ordersummary" + System.nanoTime() + "@example.com";
    }

    private String uniquePhoneNumber() {
        String suffix = String.valueOf(System.nanoTime());
        return suffix.substring(Math.max(0, suffix.length() - 10));
    }

    private String seedRegisteredUser(String fullName, String phoneNumber) {
        String email = uniqueEmail();
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.register(fullName, phoneNumber, email, PASSWORD, PASSWORD);
        Assert.assertTrue(
                registrationPage.getFeedbackMessage().contains("Account created"),
                "User registration should succeed before login."
        );
        return email;
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

    private ProductSnapshot addProductTwice(ProductListingPage productListingPage, int index) {
        ProductSnapshot product = productListingPage.getProductSnapshot(index);
        productListingPage.addProductToCart(index);
        productListingPage.addProductToCart(index);
        return product;
    }

    private List<SelectedItem> addProductsToCart(TestDataProvider.CheckoutDataset dataset) {
        ProductListingPage productListingPage = new ProductListingPage(driver).open();

        List<SelectedItem> selectedItems = new ArrayList<>();

        int firstIndex = productListingPage.findFirstAddableProductIndex();
        ProductSnapshot firstProduct = productListingPage.getProductSnapshot(firstIndex);
        if (dataset.getPrimaryQuantity() > 1) {
            firstProduct = addProductTwice(productListingPage, firstIndex);
        } else {
            productListingPage.addProductToCart(firstIndex);
        }
        selectedItems.add(new SelectedItem(firstProduct, dataset.getPrimaryQuantity()));

        if (dataset.isMultipleProducts()) {
            int secondIndex = productListingPage.findNextAddableProductIndex(firstIndex);
            ProductSnapshot secondProduct = productListingPage.getProductSnapshot(secondIndex);
            for (int i = 0; i < dataset.getSecondaryQuantity(); i++) {
                productListingPage.addProductToCart(secondIndex);
            }
            selectedItems.add(new SelectedItem(secondProduct, dataset.getSecondaryQuantity()));
        }

        return selectedItems;
    }

    private CheckoutPage proceedToCheckout() {
        CartPage cartPage = new CartPage(driver).open();
        Assert.assertTrue(cartPage.isLoaded(), "Cart page should be loaded before checkout.");
        CheckoutPage checkoutPage = cartPage.clickCheckout();
        checkoutPage.waitUntilLoaded();
        return checkoutPage;
    }

    @Test(dataProvider = "orderSummaryData", dataProviderClass = TestDataProvider.class,
            description = "T101/T102/T103/T104: Validate order summary and total calculation")
    public void validateOrderSummary(TestDataProvider.CheckoutDataset dataset) {
        String email = seedRegisteredUser(dataset.getFullName(), dataset.getPhoneNumber());
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, PASSWORD);

        List<SelectedItem> selectedItems = addProductsToCart(dataset);
        CheckoutPage checkoutPage = proceedToCheckout();

        checkoutPage.fillCheckoutForm(
                dataset.getFullName(),
                dataset.getPhoneNumber(),
                dataset.getAddress(),
                dataset.getCity(),
                dataset.getState(),
                dataset.getPincode(),
                dataset.getPaymentMethod()
        );

        OrderSummaryPage orderSummaryPage = new OrderSummaryPage(driver).waitUntilLoaded();

        Assert.assertTrue(orderSummaryPage.isDisplayed(), "Order summary section should be displayed.");
        Assert.assertTrue(orderSummaryPage.getSummaryItemCount() > 0, "Order summary should contain items.");

        int expectedQuantity = 0;
        BigDecimal expectedSubtotal = BigDecimal.ZERO;

        for (SelectedItem item : selectedItems) {
            expectedQuantity += item.quantity;
            expectedSubtotal = expectedSubtotal.add(item.unitPrice.multiply(BigDecimal.valueOf(item.quantity)));

            Assert.assertTrue(
                    orderSummaryPage.findItemByName(item.product.getName()).isPresent(),
                    "Product should be present in the order summary: " + item.product.getName()
            );

            OrderSummaryPage.SummaryItem summaryItem = orderSummaryPage.findItemByName(item.product.getName()).orElseThrow();
            Assert.assertEquals(summaryItem.getQuantity(), item.quantity, "Product quantity should be displayed correctly.");
            assertMoneyEquals(summaryItem.getUnitPrice(), item.unitPrice, "Product price should be displayed correctly.");
            assertMoneyEquals(summaryItem.getSubtotal(), item.unitPrice.multiply(BigDecimal.valueOf(item.quantity)), "Product subtotal should be displayed correctly.");
        }

        Assert.assertEquals(orderSummaryPage.getTotalQuantity(), expectedQuantity, "Total quantity should be displayed correctly.");

        BigDecimal shipping = orderSummaryPage.getOptionalChargeAmount("Shipping").orElse(BigDecimal.ZERO);
        BigDecimal tax = orderSummaryPage.getOptionalChargeAmount("Tax").orElse(BigDecimal.ZERO);
        BigDecimal calculatedExpectedTotal = expectedSubtotal.add(shipping).add(tax);

        if (orderSummaryPage.isChargeDisplayed("Shipping")) {
            assertMoneyEquals(shipping, orderSummaryPage.getOptionalChargeAmount("Shipping").orElse(BigDecimal.ZERO), "Shipping charge should be displayed correctly.");
        }

        if (orderSummaryPage.isChargeDisplayed("Tax")) {
            assertMoneyEquals(tax, orderSummaryPage.getOptionalChargeAmount("Tax").orElse(BigDecimal.ZERO), "Tax amount should be displayed correctly.");
        }

        assertMoneyEquals(orderSummaryPage.calculateExpectedTotal(), calculatedExpectedTotal, "Calculated total should match the displayed summary total formula.");
        assertMoneyEquals(orderSummaryPage.getDisplayedTotalAmount(), calculatedExpectedTotal, "Displayed total amount should match the calculated total.");
    }

    private static final class SelectedItem {
        private final ProductSnapshot product;
        private final int quantity;
        private final BigDecimal unitPrice;

        private SelectedItem(ProductSnapshot product, int quantity) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = new BigDecimal(product.getPrice().replaceAll("[^0-9.]", ""));
        }
    }
}
