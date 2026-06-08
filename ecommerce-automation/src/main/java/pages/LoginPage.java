package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private final By emailInput = By.id("email");
    private final By passwordInput = By.name("password");
    private final By signInButton = By.cssSelector("button[type='submit']");
    private final By signUpButton = By.xpath("//button[normalize-space()='Sign up']");
    private final By feedbackAlert = By.cssSelector(".alert");
    private final By titleHeading = By.xpath("//h1[normalize-space()='Welcome back']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(baseUrl() + "/login");
        waitUtils.waitForPageLoad();
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    public LoginPage openHome() {
        driver.get(baseUrl());
        waitUtils.waitForPageLoad();
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    public void enterEmail(String email) {
        type(emailInput, email);
    }

    public void enterPassword(String password) {
        type(passwordInput, password);
    }

    public void clickLogin() {
        click(signInButton);
        waitUtils.waitForPageLoad();
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public LoginPage login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
        return this;
    }

    public HomePage loginToDashboard(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
        HomePage homePage = new HomePage(driver);
        homePage.waitUntilLoaded();
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return homePage;
    }

    public LoginPage waitUntilLoaded() {
        waitUtils.waitForVisibility(emailInput);
        return this;
    }

    public RegistrationPage navigateToRegister() {
        click(signUpButton);
        waitUtils.waitForPageLoad();
        return new RegistrationPage(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(emailInput) && isDisplayed(passwordInput) && isDisplayed(titleHeading);
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
