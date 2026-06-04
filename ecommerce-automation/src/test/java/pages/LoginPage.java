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
        return isDisplayed(emailInput) && isDisplayed(passwordInput);
    }

    public HomePage login(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        click(signInButton);
        waitForDashboard();
        return new HomePage(driver, wait);
    }

    public void clickSignUp() {
        click(signUpButton);
    }

    public void clickAdminLogin() {
        click(adminLoginButton);
    }

    public String getFeedbackMessage() {
        return isDisplayed(feedbackAlert) ? textOf(feedbackAlert) : "";
    }

    private void waitForDashboard() {
        waitForUrlContains("/dashboard");
    }
}
