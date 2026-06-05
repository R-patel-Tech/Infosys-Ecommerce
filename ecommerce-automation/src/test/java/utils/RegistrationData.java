package utils;

public class RegistrationData {
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final String password;
    private final String confirmPassword;

    public RegistrationData(String fullName, String email, String phoneNumber, String password) {
        this(fullName, email, phoneNumber, password, password);
    }

    public RegistrationData(String fullName, String email, String phoneNumber, String password, String confirmPassword) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
