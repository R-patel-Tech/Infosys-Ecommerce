package components;

import base.BasePage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FilterComponent extends BasePage {
    private final By categoryFilter = By.cssSelector("select#category, .filter-category select");
    private final By priceMin = By.cssSelector("input[name='price_min'], #price-min");
    private final By priceMax = By.cssSelector("input[name='price_max'], #price-max");
    private final By applyFilters = By.cssSelector("button.apply-filters, button#applyFilters");
    private final By clearFilters = By.cssSelector("button.clear-filters, button#clearFilters");
    private final By productCard = By.cssSelector(".product-card");

    public FilterComponent(WebDriver driver) {
        super(driver);
    }

    public void applyCategory(String category) {
        WebElement select = waitForElement(categoryFilter);
        try {
            scrollToElement(select);
        } catch (Exception ignored) {
        }
        select.click();
        try {
            WebElement option = select.findElement(By.xpath(String.format(".//option[normalize-space()='%s']", category)));
            option.click();
        } catch (Exception e) {
            // fallback: choose first matching option by text
            List<WebElement> options = select.findElements(By.tagName("option"));
            for (WebElement o : options) {
                if (o.getText().trim().equalsIgnoreCase(category)) {
                    o.click();
                    break;
                }
            }
        }
        waitUtils.waitForPageLoad();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void applyPriceRange(String min, String max) {
        if (!driver.findElements(priceMin).isEmpty()) {
            sendKeys(priceMin, min);
        }
        if (!driver.findElements(priceMax).isEmpty()) {
            sendKeys(priceMax, max);
        }
        if (!driver.findElements(applyFilters).isEmpty()) {
            click(applyFilters);
        }
        waitUtils.waitForPageLoad();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void clearAllFilters() {
        if (!driver.findElements(clearFilters).isEmpty()) {
            click(clearFilters);
        }
        waitUtils.waitForPageLoad();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public int getResultsCount() {
        return driver.findElements(productCard).size();
    }
}
