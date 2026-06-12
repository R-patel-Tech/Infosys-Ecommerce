package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.ProductDetailsPage;
import pages.ProductListingPage;
import pages.RegistrationPage;
import utils.LoginUtils;
import utils.ScreenshotUtility;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebElement;

public class ProductListingTest extends BaseTest {

    private String uniqueEmail() {
        return "productuser" + System.currentTimeMillis() + "@example.com";
    }

    private String seedRegisteredUser() {
        String email = uniqueEmail();
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.register("Product User", "9876543210", email, "Test@1234", "Test@1234");
        Assert.assertTrue(registrationPage.getFeedbackMessage().contains("Account created"), "User registration should succeed before login.");
        return email;
    }

    private void pauseAfterAction() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void captureScreenshotOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtility.captureScreenshot(driver, result.getMethod().getMethodName());
        }
    }

    @Test
    public void validateProductListingPageLoadsSuccessfully() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, "Test@1234");
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        Assert.assertTrue(productListingPage.isLoaded(), "Product Listing page should load successfully.");
        Assert.assertEquals(productListingPage.getTitle(), "Raj_ecommerce", "The page title should match the expected application title.");
        Assert.assertTrue(productListingPage.getCurrentUrl().contains("/products"), "The URL should contain /products for the Product Listing page.");
        Assert.assertTrue(productListingPage.isAtLeastOneProductVisible(), "At least one product card should be visible on the Product Listing page.");
        Assert.assertTrue(productListingPage.areAllProductCardsDisplayed(), "All product cards should be displayed correctly with required content.");

        productListingPage.scrollThroughProductList();
        Assert.assertTrue(productListingPage.areProductsVisibleAfterScroll(), "Products should remain visible after scrolling through the product list.");
    }

    @Test
    public void validateProductDetailsInListing() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, "Test@1234");
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        Assert.assertTrue(productListingPage.isLoaded(), "Product Listing page should load successfully.");
        Assert.assertTrue(productListingPage.isAtLeastOneProductVisible(), "At least one product should be visible on page load.");

        List<WebElement> productCards = productListingPage.getProductCards();
        Assert.assertFalse(productCards.isEmpty(), "Product cards list should not be empty.");

        Pattern pricePattern = Pattern.compile("\\$?\\s*([0-9,.]+)");

        for (int index = 0; index < productCards.size(); index++) {
            WebElement card = productCards.get(index);
            String name = productListingPage.getProductName(card);
            String priceText = productListingPage.getProductPrice(card);

            System.out.println(String.format("Product %d: name='%s', price='%s'", index + 1, name, priceText));

            Assert.assertNotNull(name, "Product name should not be null.");
            Assert.assertFalse(name.isBlank(), "Product name should not be empty.");

            Assert.assertNotNull(priceText, "Product price should not be null.");
            Assert.assertFalse(priceText.isBlank(), "Product price should not be empty.");

            Matcher matcher = pricePattern.matcher(priceText);
            Assert.assertTrue(matcher.find(), "Product price should contain a numeric value.");

            double priceValue = Double.parseDouble(matcher.group(1).replace(",", ""));
            Assert.assertTrue(priceValue > 0, "Product price should be greater than 0.");
            pauseAfterAction();
        }
    }

    @Test
    public void verifyProductNavigationToDetailsPage() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, "Test@1234");
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        Assert.assertTrue(productListingPage.isLoaded(), "Product Listing page should load successfully.");
        Assert.assertTrue(productListingPage.isAtLeastOneProductVisible(), "At least one product should be visible on page load.");

        ProductDetailsPage productDetailsPage = productListingPage.openFirstProductDetails();
        pauseAfterAction();

        Assert.assertTrue(productDetailsPage.isLoaded(), "Product Details page should load successfully.");
        Assert.assertFalse(productDetailsPage.getProductTitle().isBlank(), "Product title should be displayed.");
        Assert.assertFalse(productDetailsPage.getProductPrice().isBlank(), "Product price should be displayed.");
        Assert.assertTrue(productDetailsPage.isProductImageDisplayed(), "Product image should be displayed.");
        Assert.assertTrue(productDetailsPage.getProductImageSource() != null && !productDetailsPage.getProductImageSource().isBlank(), "Product image source should be valid.");
        Assert.assertTrue(productDetailsPage.getCurrentUrl().contains("/products/"), "The URL should change to the product details page.");
    }
}

