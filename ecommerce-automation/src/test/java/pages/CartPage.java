package pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage extends BasePage {
    private final By pageHeading = By.xpath("//h1[normalize-space()='Your cart']");
    private final By loginPrompt = By.xpath("//p[normalize-space()='Please login to view your cart']");
    private final By emptyCartMessage = By.xpath("//p[normalize-space()='Your cart is empty.']");
    private final By cartItems = By.cssSelector(".cart-item-card");
    private final By checkoutButton = By.xpath("//button[normalize-space()='Checkout']");
    private final By clearCartButton = By.xpath("//button[normalize-space()='Clear Cart']");
    private final By refreshButton = By.xpath("//button[normalize-space()='Refresh']");
    private final By cartItemNames = By.cssSelector(".cart-item-card h3");
    private final By cartQuantityValues = By.cssSelector(".cart-quantity-value");

    public CartPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean isLoaded() {
        return isDisplayed(pageHeading) || isDisplayed(loginPrompt) || isDisplayed(emptyCartMessage);
    }

    public boolean isEmpty() {
        return isDisplayed(emptyCartMessage);
    }

    public List<String> getCartItemNames() {
        return driver.findElements(cartItemNames).stream()
                .map(WebElement::getText)
                .filter(text -> text != null && !text.isBlank())
                .map(String::trim)
                .toList();
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public String getFirstItemQuantity() {
        List<WebElement> elements = driver.findElements(cartQuantityValues);
        return elements.isEmpty() ? "" : elements.get(0).getText().trim();
    }

    public CartPage refreshCart() {
        if (isDisplayed(refreshButton)) {
            click(refreshButton);
        }
        return this;
    }

    public CartPage incrementFirstItemQuantity() {
        WebElement firstItem = firstItemCard();
        firstItem.findElements(By.xpath(".//button[normalize-space()='+']")).stream().findFirst().orElseThrow().click();
        return this;
    }

    public CartPage decrementFirstItemQuantity() {
        WebElement firstItem = firstItemCard();
        firstItem.findElements(By.xpath(".//button[normalize-space()='-']")).stream().findFirst().orElseThrow().click();
        return this;
    }

    public CartPage setFirstItemQuantity(int desiredQuantity) {
        if (desiredQuantity < 1) {
            throw new IllegalArgumentException("Desired quantity must be at least 1.");
        }

        int currentQuantity = Integer.parseInt(getFirstItemQuantity());
        while (currentQuantity < desiredQuantity) {
            incrementFirstItemQuantity();
            currentQuantity++;
        }
        while (currentQuantity > desiredQuantity) {
            decrementFirstItemQuantity();
            currentQuantity--;
        }
        return this;
    }

    public CartPage removeFirstItem() {
        WebElement firstItem = firstItemCard();
        firstItem.findElements(By.xpath(".//button[normalize-space()='Remove']")).stream().findFirst().orElseThrow().click();
        return this;
    }

    public CartPage clearCart() {
        click(clearCartButton);
        wait.until(driver -> {
            try {
                driver.switchTo().alert().accept();
                return true;
            } catch (Exception ignored) {
                return true;
            }
        });
        return this;
    }

    public CheckoutPage proceedToCheckout() {
        click(checkoutButton);
        waitForUrlContains("/checkout");
        return new CheckoutPage(driver, wait);
    }

    private WebElement firstItemCard() {
        List<WebElement> items = driver.findElements(cartItems);
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty.");
        }

        return items.get(0);
    }
}
