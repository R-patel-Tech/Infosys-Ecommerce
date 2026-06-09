package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private final By dashboardMetrics = By.cssSelector(".dashboard-metrics");
    private final By logoutButton = By.cssSelector(".navbar-logout");
    private final By profileMenu = By.cssSelector(".navbar-profile");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(dashboardMetrics);
    }

    public HomePage waitUntilLoaded() {
        waitUtils.waitForVisibility(dashboardMetrics);
        return this;
    }

    public boolean isUserLoggedIn() {
        return isLoaded() && getCurrentUrl().contains("/dashboard");
    }

    public LoginPage clickLogout() {
        try {
            if (isDisplayed(profileMenu)) {
                click(profileMenu);
            }
        } catch (Exception ignored) {
            // ignore - optional profile menu
        }

        click(logoutButton);
        waitUtils.waitForUrlContains("/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitUntilLoaded();
        return loginPage;
    }

    public LoginPage logout() {
        return clickLogout();
    }
}
