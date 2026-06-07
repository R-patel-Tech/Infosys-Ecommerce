package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.RegistrationPage;
import utils.WaitUtils;

public class WaitTest extends BaseTest {
    @Test
    public void verifyImplicitAndExplicitWaits() {
        WaitUtils waitUtils = new WaitUtils(driver);

        LoginPage loginPage = new LoginPage(driver).open();
        Assert.assertTrue(waitUtils.waitForVisibility(By.id("email")).isDisplayed());
        Assert.assertTrue(waitUtils.waitForClickable(By.cssSelector("button[type='submit']")).isDisplayed());
        waitUtils.waitForPageLoad();

        RegistrationPage registrationPage = loginPage.navigateToRegister();
        Assert.assertTrue(waitUtils.waitForVisibility(By.id("name")).isDisplayed());
        waitUtils.waitForPageLoad();
        Assert.assertTrue(registrationPage.isLoaded());
    }

    @Test
    public void verifyExplicitWaitForValidationText() {
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.enterFullName("Invalid@Name");
        registrationPage.enterPhoneNumber("9876543210");
        registrationPage.enterEmail("waittester@example.com");
        registrationPage.enterPassword("Test@1234");
        registrationPage.enterConfirmPassword("Test@1234");
        registrationPage.submit();

        WaitUtils waitUtils = new WaitUtils(driver);
        Assert.assertTrue(waitUtils.waitForText(By.cssSelector(".alert"), "Name can contain only letters"));
    }
}
