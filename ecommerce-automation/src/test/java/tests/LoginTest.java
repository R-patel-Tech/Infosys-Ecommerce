package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;

public class LoginTest extends BaseTest {

    @Test(description = "Verify that a registered user can log in successfully")
    public void userCanLogin() {
        LoginPage loginPage = new LoginPage(driver, wait);
        Assert.assertTrue(loginPage.isLoaded(), "Login page did not load.");

        HomePage homePage = loginPage.login(
                configReader.getProperty("user.email"),
                configReader.getProperty("user.password"));

        Assert.assertTrue(homePage.isLoaded(), "Dashboard did not load after login.");
    }

    @Test(description = "Verify that a user can log out successfully")
    public void userCanLogout() {
        LoginPage loginPage = new LoginPage(driver, wait);
        HomePage homePage = loginPage.login(
                configReader.getProperty("user.email"),
                configReader.getProperty("user.password"));

        LoginPage redirectedLoginPage = homePage.clickLogout();
        Assert.assertTrue(redirectedLoginPage.isLoaded(), "User was not redirected to the login page after logout.");
    }
}
