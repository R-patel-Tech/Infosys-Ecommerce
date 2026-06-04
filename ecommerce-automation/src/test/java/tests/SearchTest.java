package tests;

import base.BaseTest;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;
import pages.ProductPage;

public class SearchTest extends BaseTest {

    @Test(description = "Verify that a user can search and filter products")
    public void productSearchWorks() {
        LoginPage loginPage = new LoginPage(driver, wait);
        HomePage homePage = loginPage.login(
                configReader.getProperty("user.email"),
                configReader.getProperty("user.password"));

        ProductPage productPage = homePage.clickBrowseProducts();
        Assert.assertTrue(productPage.isLoaded(), "Product listing page did not load.");

        String searchTerm = configReader.getProperty("search.term", "Wireless");
        String category = configReader.getProperty("search.category", "Electronics");

        productPage.searchProducts(searchTerm)
                .filterByCategory(category)
                .applyFilters();

        List<String> titles = productPage.getProductTitles();
        Assert.assertFalse(titles.isEmpty(), "Search returned no visible products.");
        Assert.assertTrue(
                titles.stream().anyMatch(title -> title.toLowerCase().contains(searchTerm.toLowerCase())),
                "None of the visible products matched the search term.");
    }
}
