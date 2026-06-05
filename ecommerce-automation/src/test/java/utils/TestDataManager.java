package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TestDataManager {
    private static final DateTimeFormatter UNIQUE_SUFFIX = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final ConfigReader CONFIG = ConfigReader.getInstance();

    private TestDataManager() {
    }

    public static RegistrationData buildUniqueRegistrationData() {
        String fullName = CONFIG.getProperty("registration.fullName", "Test Automation User");
        String phoneNumber = CONFIG.getProperty("registration.phone", "9876543210");
        String emailPrefix = CONFIG.getProperty("registration.email.prefix", "automation.user");
        String password = CONFIG.getProperty("registration.password", "StrongPass@123");
        String uniqueEmail = emailPrefix + "+" + LocalDateTime.now().format(UNIQUE_SUFFIX) + "@example.com";

        return new RegistrationData(fullName, uniqueEmail, phoneNumber, password);
    }

    public static RegistrationData buildDuplicateRegistrationData(String email) {
        String fullName = CONFIG.getProperty("registration.fullName", "Test Automation User");
        String phoneNumber = CONFIG.getProperty("registration.phone", "9876543210");
        String password = CONFIG.getProperty("registration.password", "StrongPass@123");
        return new RegistrationData(fullName, email, phoneNumber, password);
    }
}
