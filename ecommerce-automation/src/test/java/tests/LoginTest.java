package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;
import pages.RegistrationPage;
import utils.LoginUtils;

public class LoginTest extends BaseTest {
    private String uniqueEmail() {
        return "login" + System.currentTimeMillis() + "@example.com";
    }

    private String seedRegisteredUser() {
        String email = uniqueEmail();
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.register("Login User", "9876543210", email, "Test@1234", "Test@1234");
        Assert.assertTrue(registrationPage.getFeedbackMessage().contains("Account created"));
        return email;
    }

    @Test
    public void automateLoginWithValidCredentials() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        HomePage homePage = loginUtils.loginWithValidCredentials(email, "Test@1234");

        Assert.assertTrue(homePage.isLoaded(), "Dashboard/home page did not load after login.");
        Assert.assertEquals(homePage.getTitle(), "Raj_ecommerce");
        Assert.assertTrue(homePage.getCurrentUrl().contains("/dashboard"));
    }

    @Test
    public void automateLoginWithInvalidCredentials() {
        LoginUtils loginUtils = new LoginUtils(driver);
        LoginPage loginPage = loginUtils.loginWithInvalidCredentials("invalid@example.com", "Wrong@1234");

        Assert.assertFalse(loginPage.getCurrentUrl().contains("/dashboard"));
        Assert.assertFalse(loginPage.getErrorMessage().isBlank());
    }

    @Test
    public void validateErrorMessagesAndMandatoryFields() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.clickLogin();

        Assert.assertFalse(loginPage.getEmailValidationMessage().isBlank());
        Assert.assertFalse(loginPage.getPasswordValidationMessage().isBlank());

        loginPage.enterEmail("invalid@example.com");
        loginPage.enterPassword("Wrong@1234");
        loginPage.clickLogin();
        Assert.assertFalse(loginPage.getErrorMessage().isBlank());
    }

    @Test
    public void implementAssertionsForLoginLogoutStatus() {
        String email = seedRegisteredUser();
        LoginUtils loginUtils = new LoginUtils(driver);
        HomePage homePage = loginUtils.loginWithValidCredentials(email, "Test@1234");

        Assert.assertTrue(homePage.isLoaded());
        Assert.assertEquals(homePage.getTitle(), "Raj_ecommerce");
        Assert.assertTrue(homePage.getCurrentUrl().contains("/dashboard"));

        LoginPage loginPage = loginUtils.logoutUser(homePage);
        Assert.assertTrue(loginPage.getCurrentUrl().contains("/login"));
    }
}
