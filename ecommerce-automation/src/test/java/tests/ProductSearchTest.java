package tests;

import base.BaseTest;
import components.SearchComponent;
import pages.RegistrationPage;
import utils.LoginUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductListingPage;
 
public class ProductSearchTest extends BaseTest {

    @Test(description = "T084: Verify users can search products successfully")
    public void testProductSearchValidAndInvalid() {
        // Register and login to ensure access to product listing
        String email = "searchuser" + System.currentTimeMillis() + "@example.com";
        RegistrationPage registration = new RegistrationPage(driver).open();
        registration.register("Search User", "9999999999", email, "Test@1234", "Test@1234");
        LoginUtils loginUtils = new LoginUtils(driver);
        loginUtils.loginWithValidCredentials(email, "Test@1234");

        ProductListingPage listing = new ProductListingPage(driver).open();
        SearchComponent search = new SearchComponent(driver);

        // Use first product's name as a valid keyword
        String firstProductName = listing.getProductName(listing.getProductCards().get(0));
        String keyword = firstProductName.split(" ")[0];

        search.search(keyword);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(search.getResults().size() > 0, "Expected at least one search result for valid keyword");
        Assert.assertTrue(search.anyResultContains(keyword), "Expected at least one result to contain the search keyword");

        // Invalid search
        String invalid = "no_such_product_12345";
        search.search(invalid);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(search.isNoResultsDisplayed() || search.getResults().isEmpty(), "Expected no results for invalid keyword");
    }
}
