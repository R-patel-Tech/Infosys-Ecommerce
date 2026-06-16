package components;

import base.BasePage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchComponent extends BasePage {
    private final By searchInput = By.cssSelector("input[type='search'], input[name='search'], #search");
    private final By searchButton = By.cssSelector("button[type='submit'].search, button.search-button, button#searchBtn");
    private final By productCard = By.cssSelector(".product-card");
    private final By productName = By.cssSelector(".product-card-body h3, .product-name");
    private final By noResults = By.xpath("//*[contains(text(),'No Products Found') or contains(text(),'No products found')]");

    public SearchComponent(WebDriver driver) {
        super(driver);
    }

    public void search(String keyword) {
        waitUtils.waitForVisibility(searchInput);
        sendKeys(searchInput, keyword);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<WebElement> buttons = driver.findElements(searchButton);
        if (!buttons.isEmpty()) {
            buttons.get(0).click();
        } else {
            visible(searchInput).submit();
        }

        waitUtils.waitForVisibility(productCard);
    }

    public List<WebElement> getResults() {
        return driver.findElements(productCard);
    }

    public boolean isNoResultsDisplayed() {
        return !driver.findElements(noResults).isEmpty();
    }

    public boolean anyResultContains(String keyword) {
        for (WebElement card : getResults()) {
            String name = "";
            try {
                WebElement n = card.findElement(productName);
                name = n.getText().trim();
            } catch (Exception ignored) {
            }
            if (name.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
