package pages;

import base.BasePage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage extends BasePage {
    private final By pageHeading = By.xpath("//h1[normalize-space()='Place your order']");
    private final By checkoutForm = By.cssSelector(".checkout-form");
    private final By checkoutSummary = By.cssSelector(".checkout-summary");
    private final By checkoutSummaryHeading = By.xpath("//aside[contains(@class,'checkout-summary')]//h2[normalize-space()='Cart summary']");
    private final By checkoutSummaryItem = By.cssSelector(".checkout-summary-item");
    private final By placeOrderButton = By.xpath("//form[contains(@class,'checkout-form')]//button[@type='submit']");

    private final By nameInput = By.id("name");
    private final By phoneInput = By.id("phone");
    private final By addressInput = By.id("address");
    private final By cityInput = By.id("city");
    private final By stateInput = By.id("state");
    private final By pincodeInput = By.id("pincode");
    private final By paymentMethodSelect = By.id("paymentMethod");

    private final By nameError = By.xpath("//label[@for='name']/following-sibling::p[contains(@class,'field-error')]");
    private final By phoneError = By.xpath("//label[@for='phone']/following-sibling::p[contains(@class,'field-error')]");
    private final By addressError = By.xpath("//label[@for='address']/following-sibling::p[contains(@class,'field-error')]");
    private final By cityError = By.xpath("//label[@for='city']/following-sibling::p[contains(@class,'field-error')]");
    private final By stateError = By.xpath("//label[@for='state']/following-sibling::p[contains(@class,'field-error')]");
    private final By pincodeError = By.xpath("//label[@for='pincode']/following-sibling::p[contains(@class,'field-error')]");
    private final By paymentMethodError = By.xpath("//label[@for='paymentMethod']/following-sibling::p[contains(@class,'field-error')]");
    private final By formErrorBanner = By.cssSelector(".form-message.error");

    private final WebDriverWait explicitWait;

    public CheckoutPage(WebDriver driver) {
        super(driver);
        this.explicitWait = new WebDriverWait(driver, Duration.ofSeconds(configReader.getIntProperty("explicitWait", 15)));
    }

    public CheckoutPage open() {
        navigateTo("/checkout");
        waitUntilLoaded();
        return this;
    }

    public CheckoutPage waitUntilLoaded() {
        explicitWait.until(driver -> isVisibleNow(pageHeading) && isVisibleNow(checkoutForm) && isVisibleNow(checkoutSummary));
        explicitWait.until(driver -> getSelectedProductCount() > 0 || isVisibleNow(formErrorBanner));
        return this;
    }

    public boolean isLoaded() {
        return isVisibleNow(pageHeading) && isVisibleNow(checkoutForm) && isVisibleNow(checkoutSummary);
    }

    public boolean isOnCheckoutPage() {
        return getCurrentUrl().contains("/checkout");
    }

    public boolean isOrderSummaryDisplayed() {
        return isVisibleNow(checkoutSummary) && isVisibleNow(checkoutSummaryHeading);
    }

    public boolean areMandatoryFieldsDisplayed() {
        return isVisibleNow(nameInput)
                && isVisibleNow(phoneInput)
                && isVisibleNow(addressInput)
                && isVisibleNow(cityInput)
                && isVisibleNow(stateInput)
                && isVisibleNow(pincodeInput)
                && isVisibleNow(paymentMethodSelect)
                && isVisibleNow(placeOrderButton);
    }

    public int getSelectedProductCount() {
        return driver.findElements(checkoutSummaryItem).size();
    }

    public List<String> getSelectedProductNames() {
        List<String> productNames = new ArrayList<>();
        for (WebElement item : driver.findElements(checkoutSummaryItem)) {
            List<WebElement> names = item.findElements(By.xpath(".//div/strong[1]"));
            if (!names.isEmpty()) {
                String name = names.get(0).getText().trim();
                if (!name.isBlank()) {
                    productNames.add(name);
                }
            }
        }
        return productNames;
    }

    public boolean isSelectedProductVisible(String productName) {
        for (String selectedName : getSelectedProductNames()) {
            if (selectedName.equals(productName)) {
                return true;
            }
        }
        return false;
    }

    public CheckoutPage waitUntilSelectedProductsVisible(int expectedCount) {
        explicitWait.until(driver -> getSelectedProductCount() == expectedCount);
        return this;
    }

    public CheckoutPage enterFullName(String value) {
        type(nameInput, value);
        return this;
    }

    public CheckoutPage enterPhoneNumber(String value) {
        type(phoneInput, value);
        return this;
    }

    public CheckoutPage enterAddress(String value) {
        type(addressInput, value);
        return this;
    }

    public CheckoutPage enterCity(String value) {
        type(cityInput, value);
        return this;
    }

    public CheckoutPage enterState(String value) {
        type(stateInput, value);
        return this;
    }

    public CheckoutPage enterPincode(String value) {
        type(pincodeInput, value);
        return this;
    }

    public CheckoutPage selectPaymentMethod(String paymentMethod) {
        new Select(visible(paymentMethodSelect)).selectByValue(paymentMethod);
        return this;
    }

    public CheckoutPage fillCheckoutForm(String fullName, String phoneNumber, String address, String city, String state, String pincode, String paymentMethod) {
        enterFullName(fullName);
        enterPhoneNumber(phoneNumber);
        enterAddress(address);
        enterCity(city);
        enterState(state);
        enterPincode(pincode);
        if (paymentMethod != null && !paymentMethod.isBlank()) {
            selectPaymentMethod(paymentMethod);
        }
        return this;
    }

    public CheckoutPage submitCheckout() {
        click(placeOrderButton);
        return this;
    }

    public CheckoutPage waitUntilOrderSuccessRedirect() {
        explicitWait.until(driver -> getCurrentUrl().contains("/order-success"));
        return this;
    }

    public CheckoutPage waitUntilRequiredFieldErrorsVisible() {
        waitUntilFieldErrorVisible(nameError);
        waitUntilFieldErrorVisible(phoneError);
        waitUntilFieldErrorVisible(addressError);
        waitUntilFieldErrorVisible(cityError);
        waitUntilFieldErrorVisible(stateError);
        waitUntilFieldErrorVisible(pincodeError);
        return this;
    }

    public CheckoutPage waitUntilInvalidInputErrorsVisible() {
        waitUntilFieldErrorVisible(phoneError);
        waitUntilFieldErrorVisible(pincodeError);
        return this;
    }

    public String getNameErrorMessage() {
        return fieldErrorText(nameError);
    }

    public String getPhoneErrorMessage() {
        return fieldErrorText(phoneError);
    }

    public String getAddressErrorMessage() {
        return fieldErrorText(addressError);
    }

    public String getCityErrorMessage() {
        return fieldErrorText(cityError);
    }

    public String getStateErrorMessage() {
        return fieldErrorText(stateError);
    }

    public String getPincodeErrorMessage() {
        return fieldErrorText(pincodeError);
    }

    public String getPaymentMethodErrorMessage() {
        return fieldErrorText(paymentMethodError);
    }

    public String getFormErrorMessage() {
        return isVisibleNow(formErrorBanner) ? textOf(formErrorBanner) : "";
    }

    private void waitUntilFieldErrorVisible(By locator) {
        explicitWait.until(driver -> {
            List<WebElement> elements = driver.findElements(locator);
            return !elements.isEmpty() && elements.get(0).isDisplayed() && !elements.get(0).getText().trim().isBlank();
        });
    }

    private String fieldErrorText(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        if (elements.isEmpty()) {
            return "";
        }

        return elements.get(0).getText().trim();
    }

    private boolean isVisibleNow(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }
}