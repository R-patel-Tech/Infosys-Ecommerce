package pages;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegistrationPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(RegistrationPage.class);

    private final By registrationHeading = By.xpath("//h1[normalize-space()='Create Account']");
    private final By fullNameInput = By.id("name");
    private final By phoneInput = By.id("phone");
    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By confirmPasswordInput = By.id("confirmPassword");
    private final By termsCheckboxCandidates = By.cssSelector(".checkbox-input");
    private final By registerButton = By.cssSelector("button[type='submit']");
    private final By feedbackAlert = By.cssSelector(".alert");
    private final By loginLink = By.xpath("//button[normalize-space()='Sign in']");

    public RegistrationPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean isLoaded() {
        return isDisplayed(registrationHeading);
    }

    public RegistrationPage enterFullName(String fullName) {
        type(fullNameInput, fullName);
        return this;
    }

    public RegistrationPage enterPhoneNumber(String phoneNumber) {
        type(phoneInput, phoneNumber);
        return this;
    }

    public RegistrationPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public RegistrationPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public RegistrationPage enterConfirmPassword(String confirmPassword) {
        type(confirmPasswordInput, confirmPassword);
        return this;
    }

    public RegistrationPage acceptTermsAndConditions() {
        List<WebElement> checkboxes = driver.findElements(termsCheckboxCandidates);
        if (checkboxes.isEmpty()) {
            logger.warn("Terms and Conditions checkbox was not found. Skipping click.");
            return this;
        }

        WebElement checkbox = checkboxes.get(0);
        waitUtils.waitForElementClickable(termsCheckboxCandidates);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        return this;
    }

    public RegistrationPage clickRegister() {
        waitUtils.waitForElementClickable(registerButton);
        click(registerButton);
        waitUtils.waitForPageLoad();
        return this;
    }

    public String getFeedbackMessage() {
        return isDisplayed(feedbackAlert) ? waitUtils.waitForElementVisible(feedbackAlert).getText() : "";
    }

    public boolean hasSuccessMessage(String message) {
        return waitUtils.waitForTextPresent(feedbackAlert, message);
    }

    public boolean hasErrorMessageContaining(String expectedFragment) {
        String actualMessage = getFeedbackMessage();
        return actualMessage != null && actualMessage.toLowerCase().contains(expectedFragment.toLowerCase());
    }

    public LoginPage clickSignIn() {
        waitUtils.waitForElementClickable(loginLink);
        click(loginLink);
        waitUtils.waitForPageLoad();
        return new LoginPage(driver, wait);
    }
}
