package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.RegistrationPage;
import utils.RegistrationData;
import utils.TestDataManager;

public class RegistrationTest extends BaseTest {
    private static String registeredEmail;

    @Test(priority = 1, description = "Verify that a user can register successfully and return to the login page")
    public void userCanRegisterSuccessfully() {
        LoginPage loginPage = new LoginPage(driver, wait);
        RegistrationPage registrationPage = loginPage.clickSignUp();

        Assert.assertTrue(registrationPage.isLoaded(), "Registration page did not load.");

        RegistrationData registrationData = TestDataManager.buildUniqueRegistrationData();
        registeredEmail = registrationData.getEmail();

        registrationPage.enterFullName(registrationData.getFullName())
                .enterEmail(registrationData.getEmail())
                .enterPhoneNumber(registrationData.getPhoneNumber())
                .enterPassword(registrationData.getPassword())
                .enterConfirmPassword(registrationData.getConfirmPassword())
                .acceptTermsAndConditions()
                .clickRegister();

        String successMessage = "Account created for " + registrationData.getFullName() + ".";
        Assert.assertTrue(registrationPage.hasSuccessMessage(successMessage),
                "Registration success message was not displayed.");
        Assert.assertTrue(registrationPage.getFeedbackMessage().contains("Account created"),
                "Unexpected registration feedback message.");

        LoginPage redirectedLoginPage = registrationPage.clickSignIn();
        Assert.assertTrue(redirectedLoginPage.isLoaded(), "User was not redirected back to the login page.");
    }

    @Test(priority = 2, description = "Verify duplicate email registration is handled gracefully")
    public void duplicateEmailIsRejected() {
        Assert.assertNotNull(registeredEmail, "Registered email was not captured from the success test.");

        LoginPage loginPage = new LoginPage(driver, wait);
        RegistrationPage registrationPage = loginPage.clickSignUp();

        Assert.assertTrue(registrationPage.isLoaded(), "Registration page did not load.");

        RegistrationData duplicateData = TestDataManager.buildDuplicateRegistrationData(registeredEmail);
        registrationPage.enterFullName(duplicateData.getFullName())
                .enterEmail(duplicateData.getEmail())
                .enterPhoneNumber(duplicateData.getPhoneNumber())
                .enterPassword(duplicateData.getPassword())
                .enterConfirmPassword(duplicateData.getConfirmPassword())
                .acceptTermsAndConditions()
                .clickRegister();

        String duplicateMessageKey = configReader.getProperty("registration.duplicate.message", "already exists");
        Assert.assertTrue(registrationPage.hasErrorMessageContaining(duplicateMessageKey)
                        || registrationPage.hasErrorMessageContaining("duplicate")
                        || registrationPage.hasErrorMessageContaining("exists"),
                "Duplicate email error message was not displayed.");
    }
}
