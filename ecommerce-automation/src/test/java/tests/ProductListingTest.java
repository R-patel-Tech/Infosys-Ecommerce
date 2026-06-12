package tests;

import base.BaseTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.ProductListingPage;
import pages.RegistrationPage;
import utils.LoginUtils;

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
            takeScreenshot(result.getMethod().getMethodName());
        }
    }

    private void takeScreenshot(String methodName) {
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }

        Path screenshotFolder = Paths.get("target", "screenshots");
        try {
            Files.createDirectories(screenshotFolder);
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Path screenshotPath = screenshotFolder.resolve(methodName + "_" + System.currentTimeMillis() + ".png");
            Files.write(screenshotPath, screenshotBytes);
        } catch (IOException ignored) {
            // preserve test result even if screenshot capture fails
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
    public void validateProductVisibility() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, "Test@1234");
        pauseAfterAction();

        ProductListingPage productListingPage = new ProductListingPage(driver).open();
        pauseAfterAction();

        Assert.assertTrue(productListingPage.isAtLeastOneProductVisible(), "At least one product should be visible on page load.");
        Assert.assertTrue(productListingPage.areAllProductCardsDisplayed(), "All displayed product cards should be visible and complete.");
        Assert.assertTrue(productListingPage.areAllProductImagesVisible(), "Each product image should be visible.");
        Assert.assertTrue(productListingPage.areAllProductNamesVisible(), "Each product name should be visible.");
        Assert.assertTrue(productListingPage.areAllProductPricesVisible(), "Each product price should be visible.");
        Assert.assertTrue(productListingPage.areAllAddToCartButtonsVisible(), "Each Add to Cart button should be visible.");
        Assert.assertTrue(productListingPage.areAllProductCardsDisplayed(), "Product cards should not be hidden or overlapped.");

        productListingPage.scrollThroughProductList();
        Assert.assertTrue(productListingPage.areProductsVisibleAfterScroll(), "Product visibility should remain consistent after scrolling.");
    }
}
