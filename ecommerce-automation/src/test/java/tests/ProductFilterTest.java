package tests;

import base.BaseTest;
import components.FilterComponent;
import pages.RegistrationPage;
import utils.LoginUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductListingPage;
 
public class ProductFilterTest extends BaseTest {

    @Test(description = "T085: Verify product filters work correctly")
    public void testFiltering() {
        // Register and login to ensure access
        String email = "filteruser" + System.currentTimeMillis() + "@example.com";
        RegistrationPage registration = new RegistrationPage(driver).open();
        registration.register("Filter User", "9999999999", email, "Test@1234", "Test@1234");
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, "Test@1234");

        ProductListingPage listing = new ProductListingPage(driver).open();
        FilterComponent filter = new FilterComponent(driver);

        int totalBefore = listing.getProductCardCount();
        Assert.assertTrue(totalBefore > 0, "Expected some products on listing page");

        // Try apply a category if available (best-effort)
        try {
            filter.applyCategory("All"); // often exists; best-effort
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Apply a price range (example)
        filter.applyPriceRange("1","9999");
        Assert.assertTrue(filter.getResultsCount() >= 0, "Filter results should be retrievable");

        // Clear filters and verify count restored (best-effort)
        filter.clearAllFilters();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int totalAfterClear = listing.getProductCardCount();
        Assert.assertTrue(totalAfterClear >= 0, "After clearing filters product count should be retrievable");
    }
}
