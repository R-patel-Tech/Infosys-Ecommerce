package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import pages.RegistrationPage;

public final class RegistrationValidationUtil {
    private static final DateTimeFormatter UNIQUE_SUFFIX = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final ConfigReader CONFIG = ConfigReader.getInstance();

    private RegistrationValidationUtil() {
    }

    public static RegistrationData buildValidData() {
        String fullName = CONFIG.getProperty("registration.fullName", "Test Automation User");
        String phoneNumber = CONFIG.getProperty("registration.phone", "9876543210");
        String emailPrefix = CONFIG.getProperty("registration.email.prefix", "automation.user");
        String password = CONFIG.getProperty("registration.password", "StrongPass@123");
        String uniqueEmail = emailPrefix + "+" + LocalDateTime.now().format(UNIQUE_SUFFIX) + "@example.com";
        return new RegistrationData(fullName, uniqueEmail, phoneNumber, password);
    }

    public static RegistrationData copyWithEmail(RegistrationData source, String email) {
        return new RegistrationData(
                source.getFullName(),
                email,
                source.getPhoneNumber(),
                source.getPassword(),
                source.getConfirmPassword());
    }

    public static RegistrationData copyWithName(RegistrationData source, String fullName) {
        return new RegistrationData(
                fullName,
                source.getEmail(),
                source.getPhoneNumber(),
                source.getPassword(),
                source.getConfirmPassword());
    }

    public static RegistrationData copyWithPhone(RegistrationData source, String phoneNumber) {
        return new RegistrationData(
                source.getFullName(),
                source.getEmail(),
                phoneNumber,
                source.getPassword(),
                source.getConfirmPassword());
    }

    public static RegistrationData copyWithPassword(RegistrationData source, String password, String confirmPassword) {
        return new RegistrationData(
                source.getFullName(),
                source.getEmail(),
                source.getPhoneNumber(),
                password,
                confirmPassword);
    }

    public static RegistrationPage fillRegistrationForm(RegistrationPage page, RegistrationData data) {
        return page.enterFullName(data.getFullName())
                .enterEmail(data.getEmail())
                .enterPhoneNumber(data.getPhoneNumber())
                .enterPassword(data.getPassword())
                .enterConfirmPassword(data.getConfirmPassword());
    }

    public static String submitAndReadMessage(RegistrationPage page, RegistrationData data) {
        fillRegistrationForm(page, data).clickRegister();
        return page.getFeedbackMessage();
    }

    public static String buildLongName(int length) {
        if (length <= 0) {
            return "";
        }
        return "A".repeat(length);
    }

    public static String buildSpecialCharacterName() {
        return "John@Doe!#";
    }

    public static String buildInvalidEmail() {
        return "invalid.email";
    }

    public static String buildInvalidPhoneShort() {
        return "12345";
    }

    public static String buildInvalidPhoneAlpha() {
        return "abcdefghij";
    }

    public static String buildInvalidPasswordShort() {
        return "Ab1@";
    }

    public static String buildPasswordMissingUppercase() {
        return "password@1";
    }

    public static String buildPasswordMissingLowercase() {
        return "PASSWORD@1";
    }

    public static String buildPasswordMissingNumber() {
        return "Password@";
    }

    public static String buildPasswordMissingSpecialCharacter() {
        return "Password1";
    }
}
