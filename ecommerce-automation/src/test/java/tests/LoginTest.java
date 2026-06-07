package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;
import pages.RegistrationPage;

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
        HomePage homePage = new LoginPage(driver).open().login(email, "Test@1234");

        Assert.assertTrue(homePage.isLoaded(), "Dashboard/home page did not load after login.");
        Assert.assertEquals(homePage.getTitle(), "Raj_ecommerce");
        Assert.assertTrue(homePage.getCurrentUrl().contains("/dashboard"));
    }

    @Test
    public void automateLoginWithInvalidCredentials() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login("invalid@example.com", "Wrong@1234");

        Assert.assertFalse(loginPage.getCurrentUrl().contains("/dashboard"));
        Assert.assertFalse(loginPage.getFeedbackMessage().isBlank());
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
        Assert.assertFalse(loginPage.getFeedbackMessage().isBlank());
    }

    @Test
    public void implementAssertionsForLoginLogoutStatus() {
        String email = seedRegisteredUser();
        HomePage homePage = new LoginPage(driver).open().login(email, "Test@1234");

        Assert.assertTrue(homePage.isLoaded());
        Assert.assertEquals(homePage.getTitle(), "Raj_ecommerce");
        Assert.assertTrue(homePage.getCurrentUrl().contains("/dashboard"));

        LoginPage loginPage = homePage.logout();
        Assert.assertTrue(loginPage.getCurrentUrl().contains("/login"));
    }
}
