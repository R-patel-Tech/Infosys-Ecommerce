package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.HomePage;
import pages.LoginPage;
import pages.ProductPage;

public class CartTest extends BaseTest {

    @Test(description = "Verify that a product can be added to the cart and updated")
    public void addToCartAndUpdateQuantity() {
        LoginPage loginPage = new LoginPage(driver, wait);
        HomePage homePage = loginPage.login(
                configReader.getProperty("user.email"),
                configReader.getProperty("user.password"));

        ProductPage productPage = homePage.clickBrowseProducts();
        productPage.addFirstDisplayedProductToCart();

        homePage = productPage.clickBack();
        CartPage cartPage = homePage.clickViewCart();

        Assert.assertTrue(cartPage.isLoaded(), "Cart page did not load.");
        Assert.assertFalse(cartPage.isEmpty(), "Cart should contain at least one product.");
        Assert.assertTrue(cartPage.getCartItemCount() >= 1, "Cart item count was not updated.");

        if (!cartPage.getFirstItemQuantity().isBlank()) {
            cartPage.setFirstItemQuantity(2);
            Assert.assertEquals(cartPage.getFirstItemQuantity(), "2", "Cart quantity was not updated to 2.");
        }

        if (!cartPage.getCartItemNames().isEmpty()) {
            cartPage.removeFirstItem();
        }
    }
}
