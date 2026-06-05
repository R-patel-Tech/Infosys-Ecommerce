package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.RegistrationPage;
import utils.RegistrationData;
import utils.RegistrationValidationUtil;

public class RegistrationFieldValidationTest extends BaseTest {

    private RegistrationPage openRegistrationPage() {
        LoginPage loginPage = new LoginPage(driver, wait);
        RegistrationPage registrationPage = loginPage.clickSignUp();
        Assert.assertTrue(registrationPage.isLoaded(), "Registration page did not load.");
        return registrationPage;
    }

    @DataProvider(name = "fullNameValidationData")
    public Object[][] fullNameValidationData() {
        RegistrationData base = RegistrationValidationUtil.buildValidData();
        return new Object[][] {
            { RegistrationValidationUtil.copyWithName(base, ""), "Name is required." },
            { RegistrationValidationUtil.copyWithName(base, RegistrationValidationUtil.buildSpecialCharacterName()),
                    "Name can contain only letters, spaces, apostrophes, periods, and hyphens." },
            { RegistrationValidationUtil.copyWithName(base, RegistrationValidationUtil.buildLongName(81)),
                    "Name must not exceed 80 characters." },
        };
    }

    @DataProvider(name = "emailValidationData")
    public Object[][] emailValidationData() {
        RegistrationData base = RegistrationValidationUtil.buildValidData();
        return new Object[][] {
            { base, true, "Account created for" },
            { RegistrationValidationUtil.copyWithEmail(base, RegistrationValidationUtil.buildInvalidEmail()),
                    false, "Enter a valid email address." },
            { RegistrationValidationUtil.copyWithEmail(base, ""), false, "Enter a valid email address." },
        };
    }

    @DataProvider(name = "phoneValidationData")
    public Object[][] phoneValidationData() {
        RegistrationData base = RegistrationValidationUtil.buildValidData();
        return new Object[][] {
            { base, true, "Account created for" },
            { RegistrationValidationUtil.copyWithPhone(base, RegistrationValidationUtil.buildInvalidPhoneShort()),
                    false, "Enter a valid phone number." },
            { RegistrationValidationUtil.copyWithPhone(base, RegistrationValidationUtil.buildInvalidPhoneAlpha()),
                    false, "Enter a valid phone number." },
        };
    }

    @DataProvider(name = "passwordValidationData")
    public Object[][] passwordValidationData() {
        RegistrationData base = RegistrationValidationUtil.buildValidData();
        return new Object[][] {
            { base, true, "Account created for" },
            { RegistrationValidationUtil.copyWithPassword(base, RegistrationValidationUtil.buildInvalidPasswordShort(),
                    RegistrationValidationUtil.buildInvalidPasswordShort()),
                    false, "Password must be 8+ characters and include uppercase, lowercase, number, and special character." },
            { RegistrationValidationUtil.copyWithPassword(base, RegistrationValidationUtil.buildPasswordMissingUppercase(),
                    RegistrationValidationUtil.buildPasswordMissingUppercase()),
                    false, "Password must be 8+ characters and include uppercase, lowercase, number, and special character." },
            { RegistrationValidationUtil.copyWithPassword(base, RegistrationValidationUtil.buildPasswordMissingLowercase(),
                    RegistrationValidationUtil.buildPasswordMissingLowercase()),
                    false, "Password must be 8+ characters and include uppercase, lowercase, number, and special character." },
            { RegistrationValidationUtil.copyWithPassword(base, RegistrationValidationUtil.buildPasswordMissingNumber(),
                    RegistrationValidationUtil.buildPasswordMissingNumber()),
                    false, "Password must be 8+ characters and include uppercase, lowercase, number, and special character." },
            { RegistrationValidationUtil.copyWithPassword(base, RegistrationValidationUtil.buildPasswordMissingSpecialCharacter(),
                    RegistrationValidationUtil.buildPasswordMissingSpecialCharacter()),
                    false, "Password must be 8+ characters and include uppercase, lowercase, number, and special character." },
        };
    }

    @DataProvider(name = "confirmPasswordData")
    public Object[][] confirmPasswordData() {
        RegistrationData base = RegistrationValidationUtil.buildValidData();
        return new Object[][] {
            { base, true, "Account created for" },
            { RegistrationValidationUtil.copyWithPassword(base, base.getPassword(), "Mismatch@123"),
                    false, "Passwords do not match." },
        };
    }

    @Test(dataProvider = "fullNameValidationData", description = "Validate full name field scenarios")
    public void validateFullNameField(RegistrationData data, String expectedMessage) {
        RegistrationPage registrationPage = openRegistrationPage();

        String actualMessage = RegistrationValidationUtil.submitAndReadMessage(registrationPage, data);
        Assert.assertTrue(actualMessage.contains(expectedMessage),
                "Unexpected full name validation message. Actual: " + actualMessage);
    }

    @Test(dataProvider = "emailValidationData", description = "Validate email field scenarios")
    public void validateEmailField(RegistrationData data, boolean shouldSucceed, String expectedMessage) {
        RegistrationPage registrationPage = openRegistrationPage();

        String actualMessage = RegistrationValidationUtil.submitAndReadMessage(registrationPage, data);

        if (shouldSucceed) {
            Assert.assertTrue(actualMessage.contains(expectedMessage),
                    "Expected registration success message for valid email. Actual: " + actualMessage);
        } else {
            Assert.assertTrue(actualMessage.contains(expectedMessage),
                    "Unexpected email validation message. Actual: " + actualMessage);
        }
    }

    @Test(dataProvider = "phoneValidationData", description = "Validate phone field scenarios")
    public void validatePhoneField(RegistrationData data, boolean shouldSucceed, String expectedMessage) {
        RegistrationPage registrationPage = openRegistrationPage();

        String actualMessage = RegistrationValidationUtil.submitAndReadMessage(registrationPage, data);

        if (shouldSucceed) {
            Assert.assertTrue(actualMessage.contains(expectedMessage),
                    "Expected registration success message for valid phone. Actual: " + actualMessage);
        } else {
            Assert.assertTrue(actualMessage.contains(expectedMessage),
                    "Unexpected phone validation message. Actual: " + actualMessage);
        }
    }

    @Test(dataProvider = "passwordValidationData", description = "Validate password field scenarios")
    public void validatePasswordField(RegistrationData data, boolean shouldSucceed, String expectedMessage) {
        RegistrationPage registrationPage = openRegistrationPage();

        String actualMessage = RegistrationValidationUtil.submitAndReadMessage(registrationPage, data);

        if (shouldSucceed) {
            Assert.assertTrue(actualMessage.contains(expectedMessage),
                    "Expected registration success message for valid password. Actual: " + actualMessage);
        } else {
            Assert.assertTrue(actualMessage.contains(expectedMessage),
                    "Unexpected password validation message. Actual: " + actualMessage);
        }
    }

    @Test(dataProvider = "confirmPasswordData", description = "Validate confirm password scenarios")
    public void validateConfirmPasswordField(RegistrationData data, boolean shouldSucceed, String expectedMessage) {
        RegistrationPage registrationPage = openRegistrationPage();

        String actualMessage = RegistrationValidationUtil.submitAndReadMessage(registrationPage, data);

        if (shouldSucceed) {
            Assert.assertTrue(actualMessage.contains(expectedMessage),
                    "Expected registration success message for matching passwords. Actual: " + actualMessage);
        } else {
            Assert.assertTrue(actualMessage.contains(expectedMessage),
                    "Unexpected confirm password validation message. Actual: " + actualMessage);
        }
    }
}
