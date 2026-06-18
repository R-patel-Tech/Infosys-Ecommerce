package pages;

import base.BasePage;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {
    private final By pageHeading = By.xpath("//h1[normalize-space()='Your cart']");
    private final By cartItem = By.cssSelector(".cart-item-card");
    private final By cartSubtotal = By.xpath("//aside[contains(@class,'cart-summary')]//div[.//span[normalize-space()='Subtotal']]//strong");
    private final By cartTotalQuantity = By.xpath("//aside[contains(@class,'cart-summary')]//div[.//span[normalize-space()='Total quantity']]//strong");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage open() {
        navigateTo("/cart");
        waitUntilLoaded();
        return this;
    }

    public CartPage waitUntilLoaded() {
        waitUtils.waitForVisibility(pageHeading);
        waitUtils.waitForVisibility(cartSubtotal);
        waitUtils.waitForVisibility(cartTotalQuantity);
        if (!driver.findElements(cartItem).isEmpty()) {
            waitUtils.waitForVisibility(cartItem);
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

    public boolean isProductVisible(String productName) {
        return findCartItem(productName) != null;
    }

    public String getProductName(String productName) {
        WebElement item = findCartItem(productName);
        return item == null ? "" : item.findElement(By.cssSelector("h3")).getText().trim();
    }

    public String getProductPrice(String productName) {
        WebElement item = findCartItem(productName);
        return item == null ? "" : item.findElement(By.cssSelector(".product-price")).getText().trim();
    }

    public String getProductSubtotal(String productName) {
        WebElement item = findCartItem(productName);
        if (item == null) {
            return "";
        }

        String subtotalText = item.findElement(By.cssSelector(".cart-item-subtotal")).getText().trim();
        return subtotalText.replace("Subtotal:", "").trim();
    }

    public int getProductQuantity(String productName) {
        WebElement item = findCartItem(productName);
        if (item == null) {
            return 0;
        }

        return Integer.parseInt(item.findElement(By.cssSelector(".cart-quantity-value")).getText().trim());
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

    private BigDecimal parseMoney(String value) {
        String sanitized = value == null ? "" : value.replaceAll("[^0-9.]", "");
        if (sanitized.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(sanitized);
    }
}
