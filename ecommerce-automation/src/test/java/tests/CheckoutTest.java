package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.HomePage;
import pages.LoginPage;
import pages.ProductPage;

public class CheckoutTest extends BaseTest {

    @Test(description = "Verify the complete checkout flow for a logged-in user")
    public void checkoutFlowWorks() {
        LoginPage loginPage = new LoginPage(driver, wait);
        HomePage homePage = loginPage.login(
                configReader.getProperty("user.email"),
                configReader.getProperty("user.password"));

        ProductPage productPage = homePage.clickBrowseProducts();
        productPage.addFirstDisplayedProductToCart();
        homePage = productPage.clickBack();
        CartPage cartPage = homePage.clickViewCart();
        Assert.assertFalse(cartPage.isEmpty(), "Cart should not be empty before checkout.");

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page did not load.");

        checkoutPage.fillAndSelectPayment(
                        configReader.getProperty("checkout.name", "Test User"),
                        configReader.getProperty("checkout.phone", "9876543210"),
                        configReader.getProperty("checkout.address", "221B Baker Street"),
                        configReader.getProperty("checkout.city", "London"),
                        configReader.getProperty("checkout.state", "London"),
                        configReader.getProperty("checkout.pincode", "560001"),
                        "COD")
                .placeOrder();

        Assert.assertTrue(checkoutPage.isOrderSuccessDisplayed(), "Order success page did not appear.");
        Assert.assertTrue(
                checkoutPage.getOrderSuccessHeading().contains("Your order has been placed"),
                "Success heading did not match.");
    }
}
