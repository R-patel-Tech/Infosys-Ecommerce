package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.RegistrationPage;

public class NavigationTest extends BaseTest {
    @Test
    public void verifyBasicNavigationBetweenLoginAndRegisterPages() {
        LoginPage loginPage = new LoginPage(driver).open();

        Assert.assertTrue(loginPage.isLoaded(), "Login page did not load.");
        Assert.assertEquals(loginPage.getTitle(), "Raj_ecommerce");
        Assert.assertTrue(loginPage.getCurrentUrl().contains("/login"));

        RegistrationPage registrationPage = loginPage.navigateToRegister();
        Assert.assertTrue(registrationPage.isLoaded(), "Registration page did not load.");
        Assert.assertEquals(registrationPage.getTitle(), "Raj_ecommerce");
        Assert.assertTrue(registrationPage.getCurrentUrl().contains("/register"));

        driver.navigate().back();
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"));

        driver.navigate().forward();
        Assert.assertTrue(driver.getCurrentUrl().contains("/register"));

        driver.navigate().refresh();
        Assert.assertTrue(driver.getCurrentUrl().contains("/register"));

        loginPage.openHome();
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"));
    }
}
