package pages;

import base.BasePage;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage extends BasePage {
    private final By pageHeading = By.xpath("//h1[normalize-space()='Your cart']");
    private final By cartItem = By.cssSelector(".cart-item-card");
    private final By cartSubtotal = By.xpath("//aside[contains(@class,'cart-summary')]//div[.//span[normalize-space()='Subtotal']]//strong");
    private final By cartTotalQuantity = By.xpath("//aside[contains(@class,'cart-summary')]//div[.//span[normalize-space()='Total quantity']]//strong");
    private final By emptyCartMessage = By.xpath("//p[normalize-space()='Your cart is empty.']");
    private final By actionSuccessMessage = By.cssSelector(".form-message.success");
    private final WebDriverWait explicitWait;

    public CartPage(WebDriver driver) {
        super(driver);
        this.explicitWait = new WebDriverWait(driver, Duration.ofSeconds(configReader.getIntProperty("explicitWait", 15)));
    }

    public CartPage open() {
        navigateTo("/cart");
        waitUntilLoaded();
        return this;
    }

    public CartPage waitUntilLoaded() {
        waitUtils.waitForVisibility(pageHeading);
        explicitWait.until(driver -> hasEmptyState(driver) || hasCartSummary(driver));

        if (hasCartSummary(driver)) {
            waitUtils.waitForVisibility(cartSubtotal);
            waitUtils.waitForVisibility(cartTotalQuantity);
            if (!driver.findElements(cartItem).isEmpty()) {
                waitUtils.waitForVisibility(cartItem);
            }
        }

        return this;
    }

    public CartPage refresh() {
        driver.navigate().refresh();
        waitUtils.waitForPageLoad();
        waitUntilLoaded();
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(pageHeading) && isDisplayed(cartSubtotal) && isDisplayed(cartTotalQuantity);
    }

    public int getCartItemCount() {
        return driver.findElements(cartItem).size();
    }

    public boolean isProductVisible(String productName) {
        return findCartItem(productName) != null;
    }

    public boolean isEmptyCartMessageVisible() {
        return isDisplayed(emptyCartMessage);
    }

    public String getEmptyCartMessage() {
        return textOf(emptyCartMessage);
    }

    public String getActionSuccessMessage() {
        return isDisplayed(actionSuccessMessage) ? textOf(actionSuccessMessage) : "";
    }

    public String getProductName(String productName) {
        WebElement item = findCartItem(productName);
        return item == null ? "" : item.findElement(By.cssSelector("h3")).getText().trim();
    }

    public String getProductPrice(String productName) {
        WebElement item = findCartItem(productName);
        return item == null ? "" : item.findElement(By.cssSelector(".product-price")).getText().trim();
    }

    public BigDecimal getProductPriceAmount(String productName) {
        return parseMoney(getProductPrice(productName));
    }

    public String getProductSubtotal(String productName) {
        WebElement item = findCartItem(productName);
        if (item == null) {
            return "";
        }

        String subtotalText = item.findElement(By.cssSelector(".cart-item-subtotal")).getText().trim();
        return subtotalText.replace("Subtotal:", "").trim();
    }

    public BigDecimal getProductSubtotalAmount(String productName) {
        return parseMoney(getProductSubtotal(productName));
    }

    public int getProductQuantity(String productName) {
        WebElement item = findCartItem(productName);
        if (item == null) {
            return 0;
        }

        return Integer.parseInt(item.findElement(By.cssSelector(".cart-quantity-value")).getText().trim());
    }

    public CartPage waitUntilProductQuantity(String productName, int expectedQuantity) {
        explicitWait.until(driver -> getProductQuantity(productName) == expectedQuantity);
        return this;
    }

    public CartPage waitUntilProductSubtotal(String productName, BigDecimal expectedSubtotal) {
        explicitWait.until(driver -> getProductSubtotalAmount(productName).subtract(expectedSubtotal).abs().compareTo(new BigDecimal("0.01")) <= 0);
        return this;
    }

    public String getCartSubtotal() {
        return textOf(cartSubtotal);
    }

    public int getCartTotalQuantity() {
        return Integer.parseInt(textOf(cartTotalQuantity));
    }

    public BigDecimal getCartSubtotalAmount() {
        return parseMoney(getCartSubtotal());
    }

    public CartPage waitUntilCartItemCount(int expectedCount) {
        explicitWait.until(driver -> getCartItemCount() == expectedCount);
        return this;
    }

    public CartPage waitUntilCartTotalQuantity(int expectedQuantity) {
        explicitWait.until(driver -> getCartTotalQuantity() == expectedQuantity);
        return this;
    }

    public CartPage waitUntilCartSubtotal(BigDecimal expectedSubtotal) {
        explicitWait.until(driver -> getCartSubtotalAmount().subtract(expectedSubtotal).abs().compareTo(new BigDecimal("0.01")) <= 0);
        return this;
    }

    public CartPage waitUntilProductNotVisible(String productName) {
        explicitWait.until(driver -> findCartItem(productName) == null);
        return this;
    }

    public CartPage waitUntilEmptyCartMessage() {
        explicitWait.until(driver -> hasEmptyState(driver));
        return this;
    }

    public boolean isDecreaseQuantityDisabled(String productName) {
        WebElement item = findCartItem(productName);
        if (item == null) {
            return false;
        }

        List<WebElement> buttons = item.findElements(By.cssSelector(".cart-quantity-controls button"));
        if (buttons.isEmpty()) {
            return false;
        }

        return !buttons.get(0).isEnabled();
    }

    public CartPage increaseQuantity(String productName) {
        clickQuantityButton(productName, 1);
        return this;
    }

    public CartPage decreaseQuantity(String productName) {
        clickQuantityButton(productName, 0);
        return this;
    }

    public CartPage removeItem(String productName) {
        WebElement item = findCartItem(productName);
        if (item == null) {
            throw new IllegalArgumentException("Cart item not found for product: ".concat(productName));
        }

        List<WebElement> buttons = item.findElements(By.cssSelector(".cart-item-controls button"));
        if (buttons.isEmpty()) {
            throw new IllegalStateException("Remove button not found for product: ".concat(productName));
        }

        buttons.get(buttons.size() - 1).click();
        return this;
    }

    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }

    private WebElement findCartItem(String productName) {
        List<WebElement> items = driver.findElements(cartItem);
        for (WebElement item : items) {
            String itemName = item.findElement(By.cssSelector("h3")).getText().trim();
            if (itemName.equals(productName)) {
                return item;
            }
        }

        return null;
    }

    private void clickQuantityButton(String productName, int buttonIndex) {
        WebElement item = findCartItem(productName);
        if (item == null) {
            throw new IllegalArgumentException("Cart item not found for product: ".concat(productName));
        }

        List<WebElement> buttons = item.findElements(By.cssSelector(".cart-quantity-controls button"));
        if (buttons.size() <= buttonIndex) {
            throw new IllegalStateException("Quantity control button not found for product: ".concat(productName));
        }

        buttons.get(buttonIndex).click();
    }

    private BigDecimal parseMoney(String value) {
        String sanitized = value == null ? "" : value.replaceAll("[^0-9.]", "");
        if (sanitized.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(sanitized);
    }

    private boolean hasEmptyState(WebDriver driver) {
        List<WebElement> emptyMessages = driver.findElements(emptyCartMessage);
        return !emptyMessages.isEmpty() && emptyMessages.get(0).isDisplayed();
    }

    private boolean hasCartSummary(WebDriver driver) {
        return !driver.findElements(cartSubtotal).isEmpty() && !driver.findElements(cartTotalQuantity).isEmpty();
    }
}
