package pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductPage extends BasePage {
    private final By pageHeading = By.xpath("//h1[normalize-space()='Featured products']");
    private final By searchField = By.id("product-search");
    private final By categorySelect = By.id("product-category");
    private final By applyFiltersButton = By.xpath("//button[normalize-space()='Apply Filters']");
    private final By resetButton = By.xpath("//button[normalize-space()='Reset']");
    private final By refreshButton = By.xpath("//button[normalize-space()='Refresh']");
    private final By backButton = By.xpath("//button[normalize-space()='Back']");
    private final By productCards = By.cssSelector(".product-card-modern");
    private final By productTitles = By.cssSelector(".product-card-modern h3");
    private final By addToCartButtons = By.cssSelector(".product-card-modern .product-action-button");
    private final By productCount = By.cssSelector(".product-count");

    public ProductPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean isLoaded() {
        return isDisplayed(pageHeading);
    }

    public ProductPage searchProducts(String term) {
        type(searchField, term);
        return this;
    }

    public ProductPage filterByCategory(String category) {
        WebElement selectElement = visible(categorySelect);
        new Select(selectElement).selectByVisibleText(category);
        return this;
    }

    public ProductPage applyFilters() {
        click(applyFiltersButton);
        wait.until(driver -> isDisplayed(productCards) || isDisplayed(productCount));
        return this;
    }

    public ProductPage resetFilters() {
        click(resetButton);
        return this;
    }

    public ProductPage refreshProducts() {
        click(refreshButton);
        wait.until(driver -> isDisplayed(productCards) || isDisplayed(productCount));
        return this;
    }

    public HomePage clickBack() {
        click(backButton);
        waitForUrlContains("/dashboard");
        return new HomePage(driver, wait);
    }

    public int getVisibleProductCount() {
        return driver.findElements(productCards).size();
    }

    public List<String> getProductTitles() {
        List<String> titles = new ArrayList<>();
        for (WebElement element : driver.findElements(productTitles)) {
            String text = element.getText();
            if (text != null && !text.isBlank()) {
                titles.add(text.trim());
            }
        }
        return titles;
    }

    public ProductPage addFirstDisplayedProductToCart() {
        List<WebElement> buttons = driver.findElements(addToCartButtons);
        if (buttons.isEmpty()) {
            throw new IllegalStateException("No product cards are available to add to cart.");
        }

        buttons.get(0).click();
        return this;
    }

    public ProductPage addProductToCartByIndex(int index) {
        List<WebElement> buttons = driver.findElements(addToCartButtons);
        if (index < 0 || index >= buttons.size()) {
            throw new IndexOutOfBoundsException("Invalid product index: " + index);
        }

        buttons.get(index).click();
        return this;
    }
}
