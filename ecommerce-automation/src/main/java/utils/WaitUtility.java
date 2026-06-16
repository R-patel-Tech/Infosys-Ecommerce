package utils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtility {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtility(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInstance().getIntProperty("explicitWait", 15)));
    }

    public List<WebElement> waitForAll(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement waitForFirst(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean waitForUrlContains(String fragment) {
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    public <T> T retryOnStale(Callable<T> action, int attempts) throws Exception {
        int tries = 0;
        while (true) {
            try {
                return action.call();
            } catch (StaleElementReferenceException ex) {
                tries++;
                if (tries >= attempts) throw ex;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            }
        }
    }
}
