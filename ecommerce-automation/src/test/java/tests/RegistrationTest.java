package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RegistrationPage;

public class RegistrationTest extends BaseTest {
    private String uniqueEmail() {
        return "selenium" + System.currentTimeMillis() + "@example.com";
    }

    @Test
    public void automateUserRegistrationFlow() {
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.register("Selenium User", "9876543210", uniqueEmail(), "Test@1234", "Test@1234");

        Assert.assertTrue(registrationPage.getFeedbackMessage().contains("Account created"));
    }

    @Test
    public void validateMandatoryAndBlankFieldMessages() {
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.submit();

        Assert.assertFalse(registrationPage.getNameValidationMessage().isBlank());
        Assert.assertFalse(registrationPage.getPhoneValidationMessage().isBlank());
        Assert.assertFalse(registrationPage.getEmailValidationMessage().isBlank());
        Assert.assertFalse(registrationPage.getPasswordValidationMessage().isBlank());
    }

    @Test
    public void validateEmailFormatAndInvalidInputMessages() {
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.enterFullName("Valid User");
        registrationPage.enterPhoneNumber("9876543210");
        registrationPage.enterEmail("invalid-email");
        registrationPage.enterPassword("Test@1234");
        registrationPage.enterConfirmPassword("Test@1234");
        registrationPage.submit();

        Assert.assertFalse(registrationPage.getEmailValidationMessage().isBlank());
    }

    @Test
    public void validateInvalidInputMessage() {
        RegistrationPage registrationPage = new RegistrationPage(driver).open();
        registrationPage.enterFullName("Invalid@Name");
        registrationPage.enterPhoneNumber("9876543210");
        registrationPage.enterEmail(uniqueEmail());
        registrationPage.enterPassword("Test@1234");
        registrationPage.enterConfirmPassword("Test@1234");
        registrationPage.submit();

        Assert.assertTrue(registrationPage.getFeedbackMessage().contains("Name can contain only letters"));
    }
}
