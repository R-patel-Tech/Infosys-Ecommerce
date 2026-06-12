package pages;

import base.BasePage;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductListingPage extends BasePage {
    private final By pageHeading = By.xpath("//h1[normalize-space()='Featured products']");
    private final By productGrid = By.cssSelector(".product-grid");
    private final By productCard = By.cssSelector(".product-card");
    private final By productImage = By.cssSelector(".product-image");
    private final By productName = By.cssSelector(".product-card-body h3");
    private final By productPrice = By.cssSelector(".product-price");
    private final By addToCartButton = By.cssSelector(".product-action-button");

    public ProductListingPage(WebDriver driver) {
        super(driver);
    }

    public ProductListingPage open() {
        navigateTo("/products");
        waitUntilLoaded();
        return this;
    }

    public ProductListingPage waitUntilLoaded() {
        waitUtils.waitForVisibility(pageHeading);
        waitUtils.waitForVisibility(productGrid);
        waitUtils.waitForVisibility(productCard);
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(pageHeading) && isDisplayed(productGrid) && isDisplayed(productCard);
    }

    public int getProductCardCount() {
        return driver.findElements(productCard).size();
    }

    public boolean isAtLeastOneProductVisible() {
        return getProductCardCount() > 0;
    }

    public boolean areAllProductCardsDisplayed() {
        List<WebElement> cards = driver.findElements(productCard);
        if (cards.isEmpty()) {
            return false;
        }

        for (WebElement card : cards) {
            if (!isCardVisible(card)) {
                return false;
            }
            if (!isCardContentValid(card)) {
                return false;
            }
        }

        return true;
    }

    public boolean isCardVisible(WebElement card) {
        try {
            return card.isDisplayed() && card.getRect().getHeight() > 0 && card.getRect().getWidth() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCardContentValid(WebElement card) {
        try {
            WebElement image = card.findElement(productImage);
            WebElement name = card.findElement(productName);
            WebElement price = card.findElement(productPrice);
            WebElement button = card.findElement(addToCartButton);

            String imageSrc = image.getAttribute("src");
            String nameText = name.getText().trim();
            String priceText = price.getText().trim();

            return image.isDisplayed()
                    && imageSrc != null
                    && !imageSrc.isBlank()
                    && name.isDisplayed()
                    && !nameText.isEmpty()
                    && price.isDisplayed()
                    && !priceText.isEmpty()
                    && button.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCardNotOverlapped(WebElement card) {
        try {
            String script = "const card = arguments[0];" +
                    "const rect = card.getBoundingClientRect();" +
                    "const cx = rect.left + rect.width / 2;" +
                    "const cy = rect.top + rect.height / 2;" +
                    "const top = document.elementFromPoint(cx, cy);" +
                    "return top === card || card.contains(top);";
            Object result = ((JavascriptExecutor) driver).executeScript(script, card);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    public void scrollThroughProductList() {
        List<WebElement> cards = new ArrayList<>(driver.findElements(productCard));
        for (WebElement card : cards) {
            scrollTo(card);
            pauseForObservation();
        }
    }

    public boolean areProductsVisibleAfterScroll() {
        scrollThroughProductList();
        return areAllProductCardsDisplayed();
    }

    public boolean areAllProductImagesVisible() {
        return areAllProductElementsVisible(productImage);
    }

    public boolean areAllProductNamesVisible() {
        return areAllProductElementsVisible(productName);
    }

    public boolean areAllProductPricesVisible() {
        return areAllProductElementsVisible(productPrice);
    }

    public boolean areAllAddToCartButtonsVisible() {
        return areAllProductElementsVisible(addToCartButton);
    }

    private boolean areAllProductElementsVisible(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        if (elements.isEmpty()) {
            return false;
        }

        for (WebElement element : elements) {
            if (!element.isDisplayed()) {
                return false;
            }
        }

        return true;
    }

    private void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    private void pauseForObservation() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
