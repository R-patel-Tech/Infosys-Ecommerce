package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage extends BasePage {
    private final By dashboardHeading = By.xpath("//h1[normalize-space()='Welcome back']");
    private final By browseProductsButton = By.xpath("//button[normalize-space()='Browse Products']");
    private final By viewCartButton = By.xpath("//button[normalize-space()='View Cart']");
    private final By orderHistoryButton = By.xpath("//button[normalize-space()='Order History']");
    private final By profileButton = By.cssSelector("button[aria-label='User profile']");
    private final By logoutButton = By.xpath("//button[normalize-space()='Logout']");
    private final By dashboardNavbar = By.cssSelector(".dashboard-navbar");

    public HomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean isLoaded() {
        return isDisplayed(dashboardHeading) || isDisplayed(dashboardNavbar);
    }

    public ProductPage clickBrowseProducts() {
        click(browseProductsButton);
        waitForUrlContains("/products");
        return new ProductPage(driver, wait);
    }

    public CartPage clickViewCart() {
        click(viewCartButton);
        waitForUrlContains("/cart");
        return new CartPage(driver, wait);
    }

    public LoginPage clickLogout() {
        click(logoutButton);
        waitForUrlContains("/login");
        return new LoginPage(driver, wait);
    }

    public void openProfile() {
        click(profileButton);
    }

    public void openOrderHistory() {
        click(orderHistoryButton);
        waitForUrlContains("/orders");
    }
}
