package utils;

import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AlertUtils {
    private static final Logger logger = LogManager.getLogger(AlertUtils.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public AlertUtils(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    public void acceptAlert() {
        try {
            logger.debug("Accepting alert");
            getAlert().accept();
        } catch (NoAlertPresentException exception) {
            logger.error("No alert present to accept", exception);
            throw new IllegalStateException("No alert present to accept.", exception);
        }
    }

    public void dismissAlert() {
        try {
            logger.debug("Dismissing alert");
            getAlert().dismiss();
        } catch (NoAlertPresentException exception) {
            logger.error("No alert present to dismiss", exception);
            throw new IllegalStateException("No alert present to dismiss.", exception);
        }
    }

    public String getAlertText() {
        try {
            logger.debug("Reading alert text");
            return getAlert().getText();
        } catch (NoAlertPresentException exception) {
            logger.error("No alert present to read text from", exception);
            throw new IllegalStateException("No alert present to read text from.", exception);
        }
    }

    public void sendTextToAlert(String text) {
        try {
            logger.debug("Sending text to alert");
            getAlert().sendKeys(text);
        } catch (NoAlertPresentException exception) {
            logger.error("No prompt alert present to send text to", exception);
            throw new IllegalStateException("No prompt alert present to send text to.", exception);
        }
    }

    public Alert waitForAlert() {
        try {
            logger.debug("Waiting for JavaScript alert");
            return wait.until(ExpectedConditions.alertIsPresent());
        } catch (TimeoutException exception) {
            logger.error("Timed out waiting for alert", exception);
            throw new IllegalStateException("Alert did not appear in time.", exception);
        }
    }

    public boolean waitForModalVisible(By locator) {
        try {
            logger.debug("Waiting for modal to be visible: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)) != null;
        } catch (TimeoutException exception) {
            logger.error("Timed out waiting for modal: {}", locator, exception);
            throw new IllegalStateException("Modal dialog not visible: " + locator, exception);
        }
    }

    public boolean waitForTextPresent(By locator, String text) {
        try {
            logger.debug("Waiting for text '{}' in element: {}", text, locator);
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException exception) {
            logger.error("Timed out waiting for text '{}' in element: {}", text, locator, exception);
            throw new IllegalStateException("Expected text not present: " + text, exception);
        }
    }

    private Alert getAlert() {
        try {
            return driver.switchTo().alert();
        } catch (NoAlertPresentException exception) {
            throw exception;
        } catch (RuntimeException runtimeException) {
            logger.error("Unexpected error while switching to alert", runtimeException);
            throw runtimeException;
        }
    }
}
