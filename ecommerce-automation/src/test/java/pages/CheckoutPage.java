package pages;

import java.util.LinkedHashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage extends BasePage {
    private final By pageHeading = By.xpath("//h1[normalize-space()='Place your order']");
    private final By nameField = By.id("name");
    private final By phoneField = By.id("phone");
    private final By addressField = By.id("address");
    private final By cityField = By.id("city");
    private final By stateField = By.id("state");
    private final By pincodeField = By.id("pincode");
    private final By paymentMethodSelect = By.id("paymentMethod");
    private final By placeOrderButton = By.xpath("//button[normalize-space()='Place Order']");
    private final By cancelButton = By.xpath("//button[normalize-space()='Cancel']");
    private final By orderSuccessHeading = By.xpath("//h1[normalize-space()='Your order has been placed']");
    private final By orderSuccessContainer = By.cssSelector(".success-shell");

    public CheckoutPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean isLoaded() {
        return isDisplayed(pageHeading);
    }

    public CheckoutPage fillShippingDetails(String name, String phone, String address, String city, String state, String pincode) {
        Map<By, String> fields = new LinkedHashMap<>();
        fields.put(nameField, name);
        fields.put(phoneField, phone);
        fields.put(addressField, address);
        fields.put(cityField, city);
        fields.put(stateField, state);
        fields.put(pincodeField, pincode);

        for (Map.Entry<By, String> entry : fields.entrySet()) {
            type(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public CheckoutPage selectPaymentMethod(String method) {
        WebElement element = visible(paymentMethodSelect);
        new Select(element).selectByVisibleText(resolvePaymentMethod(method));
        return this;
    }

    public CheckoutPage fillAndSelectPayment(String name, String phone, String address, String city, String state, String pincode,
                                             String paymentMethod) {
        fillShippingDetails(name, phone, address, city, state, pincode);
        selectPaymentMethod(paymentMethod);
        return this;
    }

    public CheckoutPage cancel() {
        click(cancelButton);
        return this;
    }

    public CheckoutPage placeOrder() {
        click(placeOrderButton);
        wait.until(driver -> driver.getCurrentUrl().contains("/order-success") || isDisplayed(orderSuccessHeading));
        return this;
    }

    public boolean isOrderSuccessDisplayed() {
        return isDisplayed(orderSuccessHeading) || isDisplayed(orderSuccessContainer);
    }

    public String getOrderSuccessHeading() {
        return isDisplayed(orderSuccessHeading) ? textOf(orderSuccessHeading) : "";
    }

    private String resolvePaymentMethod(String method) {
        String normalized = method == null ? "" : method.trim().toUpperCase();
        return switch (normalized) {
            case "COD", "CASH ON DELIVERY" -> "Cash on Delivery";
            case "UPI" -> "UPI";
            case "CARD" -> "Card";
            case "NET_BANKING", "NET BANKING" -> "Net Banking";
            case "WALLET" -> "Wallet";
            default -> method;
        };
    }
}
