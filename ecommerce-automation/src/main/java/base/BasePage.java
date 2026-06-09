package base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ConfigReader;
import utils.WaitUtils;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WaitUtils waitUtils;
    protected final ConfigReader configReader;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.configReader = ConfigReader.getInstance();
    }

    protected String getBaseUrl() {
        return configReader.getProperty("baseUrl", "http://localhost:5173");
    }

    protected WebElement visible(By locator) {
        return waitUtils.waitForVisibility(locator);
    }

    protected WebElement clickable(By locator) {
        return waitUtils.waitForClickable(locator);
    }

    protected void click(By locator) {
        clickable(locator).click();
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(text);
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected String textOf(By locator) {
        return visible(locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        return waitUtils.isElementVisible(locator);
    }

    protected void navigateTo(String relativePath) {
        driver.get(getBaseUrl() + relativePath);
        waitUtils.waitForPageLoad();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
