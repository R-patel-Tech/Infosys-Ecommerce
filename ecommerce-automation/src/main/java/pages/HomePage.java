package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private final By welcomeHeading = By.xpath("//h1[normalize-space()='Welcome back']");
    private final By logoutButton = By.cssSelector(".navbar-logout");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(welcomeHeading);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public LoginPage logout() {
        click(logoutButton);
        waitUtils.waitForPageLoad();
        return new LoginPage(driver);
    }
}
