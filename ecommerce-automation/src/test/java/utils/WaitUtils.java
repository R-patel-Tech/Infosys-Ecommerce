package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public WebElement waitForElementVisible(By locator) {
        try {
            logger.debug("Waiting for element to be visible: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException timeoutException) {
            logger.error("Timed out waiting for visible element: {}", locator, timeoutException);
            throw new IllegalStateException("Element not visible: " + locator, timeoutException);
        }
    }

    public WebElement waitForElementClickable(By locator) {
        try {
            logger.debug("Waiting for element to be clickable: {}", locator);
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException timeoutException) {
            logger.error("Timed out waiting for clickable element: {}", locator, timeoutException);
            throw new IllegalStateException("Element not clickable: " + locator, timeoutException);
        }
    }

    public boolean waitForTextPresent(By locator, String text) {
        try {
            logger.debug("Waiting for text '{}' to appear in element: {}", text, locator);
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException timeoutException) {
            logger.error("Timed out waiting for text '{}' in element: {}", text, locator, timeoutException);
            throw new IllegalStateException("Text not present in element: " + locator + " text: " + text, timeoutException);
        }
    }

    public void waitForPageLoad() {
        try {
            logger.debug("Waiting for page load to complete");
            wait.until(webDriver -> {
                if (!(webDriver instanceof JavascriptExecutor javascriptExecutor)) {
                    return true;
                }
                Object readyState = javascriptExecutor.executeScript("return document.readyState");
                return "complete".equals(String.valueOf(readyState));
            });
        } catch (TimeoutException timeoutException) {
            logger.error("Timed out waiting for page load", timeoutException);
            throw new IllegalStateException("Page did not finish loading in time.", timeoutException);
        }
    }

    public boolean waitForUrlContains(String fragment) {
        try {
            logger.debug("Waiting for URL to contain fragment: {}", fragment);
            return wait.until(ExpectedConditions.urlContains(fragment));
        } catch (TimeoutException timeoutException) {
            logger.error("Timed out waiting for URL fragment: {}", fragment, timeoutException);
            throw new IllegalStateException("URL did not contain fragment: " + fragment, timeoutException);
        }
    }

    public boolean waitForUrlToBe(String url) {
        try {
            logger.debug("Waiting for URL to be: {}", url);
            return wait.until(ExpectedConditions.urlToBe(url));
        } catch (TimeoutException timeoutException) {
            logger.error("Timed out waiting for URL: {}", url, timeoutException);
            throw new IllegalStateException("URL did not match: " + url, timeoutException);
        }
    }
}
