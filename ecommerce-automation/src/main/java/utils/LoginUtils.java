package utils;

import org.openqa.selenium.WebDriver;
import pages.HomePage;
import pages.LoginPage;

/**
 * Utility class for reusable login and logout operations.
 */
public class LoginUtils {
    private final WebDriver driver;

    public LoginUtils(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Opens the login page, submits valid credentials, and returns the resulting HomePage.
     */
    public HomePage loginWithValidCredentials(String email, String password) {
        return new LoginPage(driver).open().loginToDashboard(email, password);
    }

    /**
     * Opens the login page, submits invalid credentials, and returns the LoginPage for validation.
     */
    public LoginPage loginWithInvalidCredentials(String email, String password) {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login(email, password);
        return loginPage;
    }

    /**
     * Logs out the user from the provided HomePage and returns the LoginPage.
     */
    public LoginPage logoutUser(HomePage homePage) {
        return homePage.logout();
    }
}
