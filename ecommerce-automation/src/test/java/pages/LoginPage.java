package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends BasePage {
    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By signInButton = By.cssSelector("button[type='submit']");
    private final By signUpButton = By.xpath("//button[normalize-space()='Sign up']");
    private final By adminLoginButton = By.xpath("//button[normalize-space()='Admin Login']");
    private final By loginHeading = By.xpath("//h1[normalize-space()='Welcome back']");
    private final By feedbackAlert = By.cssSelector(".alert");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean isLoaded() {
        return waitUtils.waitForElementVisible(emailInput) != null
                && waitUtils.waitForElementVisible(passwordInput) != null;
    }

    public HomePage login(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        click(signInButton);
        waitUtils.waitForPageLoad();
        waitForDashboard();
        return new HomePage(driver, wait);
    }

    public RegistrationPage clickSignUp() {
        click(signUpButton);
        waitUtils.waitForPageLoad();
        waitForUrlContains("/register");
        return new RegistrationPage(driver, wait);
    }

    public void clickAdminLogin() {
        click(adminLoginButton);
    }

    public String getFeedbackMessage() {
        return isDisplayed(feedbackAlert) ? waitUtils.waitForElementVisible(feedbackAlert).getText() : "";
    }

    public String waitForFeedbackMessage(String expectedText) {
        waitUtils.waitForTextPresent(feedbackAlert, expectedText);
        return getFeedbackMessage();
    }

    private void waitForDashboard() {
        waitForUrlContains("/dashboard");
    }
}
