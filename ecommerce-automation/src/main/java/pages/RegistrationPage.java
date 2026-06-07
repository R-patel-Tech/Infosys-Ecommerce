package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegistrationPage extends BasePage {
    private final By nameInput = By.id("name");
    private final By phoneInput = By.name("phone");
    private final By emailInput = By.cssSelector("input[type='email']");
    private final By passwordInput = By.xpath("//input[@name='password']");
    private final By confirmPasswordInput = By.id("confirmPassword");
    private final By registerButton = By.cssSelector("button[type='submit']");
    private final By signInButton = By.xpath("//button[normalize-space()='Sign in']");
    private final By feedbackAlert = By.cssSelector(".alert");
    private final By titleHeading = By.xpath("//h1[normalize-space()='Create Account']");

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    public RegistrationPage open() {
        driver.get(baseUrl() + "/register");
        waitUtils.waitForPageLoad();
        return this;
    }

    public void enterFullName(String fullName) {
        type(nameInput, fullName);
    }

    public void enterPhoneNumber(String phoneNumber) {
        type(phoneInput, phoneNumber);
    }

    public void enterEmail(String email) {
        type(emailInput, email);
    }

    public void enterPassword(String password) {
        type(passwordInput, password);
    }

    public void enterConfirmPassword(String confirmPassword) {
        type(confirmPasswordInput, confirmPassword);
    }

    public void submit() {
        click(registerButton);
        waitUtils.waitForPageLoad();
    }

    public void register(String fullName, String phoneNumber, String email, String password, String confirmPassword) {
        enterFullName(fullName);
        enterPhoneNumber(phoneNumber);
        enterEmail(email);
        enterPassword(password);
        enterConfirmPassword(confirmPassword);
        submit();
    }

    public LoginPage navigateToLogin() {
        click(signInButton);
        waitUtils.waitForPageLoad();
        return new LoginPage(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(nameInput) && isDisplayed(phoneInput) && isDisplayed(emailInput) && isDisplayed(titleHeading);
    }

    public String getFeedbackMessage() {
        return isDisplayed(feedbackAlert) ? textOf(feedbackAlert) : "";
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getNameValidationMessage() {
        return driver.findElement(nameInput).getAttribute("validationMessage");
    }

    public String getPhoneValidationMessage() {
        return driver.findElement(phoneInput).getAttribute("validationMessage");
    }

    public String getEmailValidationMessage() {
        return driver.findElement(emailInput).getAttribute("validationMessage");
    }

    public String getPasswordValidationMessage() {
        return driver.findElement(passwordInput).getAttribute("validationMessage");
    }

    private String baseUrl() {
        return utils.ConfigReader.getInstance().getProperty("baseUrl", "http://localhost:5173");
    }
}
