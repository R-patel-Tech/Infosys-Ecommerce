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
        return waitUtils.waitForElementVisible(dashboardHeading) != null
                || waitUtils.waitForElementVisible(dashboardNavbar) != null;
    }

    public ProductPage clickBrowseProducts() {
        waitUtils.waitForElementClickable(browseProductsButton);
        click(browseProductsButton);
        waitUtils.waitForPageLoad();
        waitForUrlContains("/products");
        return new ProductPage(driver, wait);
    }

    public CartPage clickViewCart() {
        waitUtils.waitForElementClickable(viewCartButton);
        click(viewCartButton);
        waitUtils.waitForPageLoad();
        waitForUrlContains("/cart");
        return new CartPage(driver, wait);
    }

    public LoginPage clickLogout() {
        waitUtils.waitForElementClickable(logoutButton);
        click(logoutButton);
        waitUtils.waitForPageLoad();
        waitForUrlContains("/login");
        return new LoginPage(driver, wait);
    }

    public void openProfile() {
        waitUtils.waitForElementClickable(profileButton);
        click(profileButton);
    }

    public void openOrderHistory() {
        waitUtils.waitForElementClickable(orderHistoryButton);
        click(orderHistoryButton);
        waitUtils.waitForPageLoad();
        waitForUrlContains("/orders");
    }
}
