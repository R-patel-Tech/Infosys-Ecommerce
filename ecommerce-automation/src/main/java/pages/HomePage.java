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

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public LoginPage logout() {
        // Some apps require opening a profile/user menu before logout
        try {
            if (isDisplayed(profileMenu)) {
                click(profileMenu);
            }
        } catch (Exception ignored) {
            // ignore - optional profile menu
        }

        click(logoutButton);
        waitUtils.waitForUrlContains("/login");
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LoginPage loginPage = new LoginPage(driver);
        loginPage.waitUntilLoaded();
        return loginPage;
    }
}
