package pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.WaitUtils;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WaitUtils waitUtils;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        this.waitUtils = new WaitUtils(driver, wait);
    }

    protected WebElement visible(By locator) {
        return waitUtils.waitForElementVisible(locator);
    }

    protected WebElement clickable(By locator) {
        return waitUtils.waitForElementClickable(locator);
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String textOf(By locator) {
        return visible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    protected void waitForUrlContains(String fragment) {
        waitUtils.waitForUrlContains(fragment);
    }

    protected void waitForUrlToBe(String url) {
        waitUtils.waitForUrlToBe(url);
    }

    protected void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    protected WebDriverWait createShortWait(Duration timeout) {
        return new WebDriverWait(driver, timeout);
    }
}
