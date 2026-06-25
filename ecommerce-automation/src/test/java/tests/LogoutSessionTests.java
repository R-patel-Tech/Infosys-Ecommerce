package tests;

import base.BaseTest;
import base.DriverFactory;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WindowType;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;
import utils.LoginUtils;
import utils.WaitUtils;

import java.time.Duration;
import java.util.Set;

public class LogoutSessionTests extends BaseTest {

    @Test(description = "T076: Automate Logout Functionality")
    public void testLogoutFunctionality() {
        String email = configReader.getProperty("username", "testuser@example.com");
        String password = configReader.getProperty("password", "P@ssw0rd");
        String baseUrl = configReader.getProperty("baseUrl", "http://localhost:5173");

        LoginUtils loginUtils = new LoginUtils(driver);
        HomePage home = loginUtils.loginWithValidCredentials(email, password);

        Assert.assertTrue(home.isLoaded(), "User should be logged in and home should be loaded");

        LoginPage afterLogout = loginUtils.logoutUser(home);

        Assert.assertTrue(afterLogout.isLoaded(), "Login page should be displayed after logout");
        Assert.assertTrue(afterLogout.getCurrentUrl().contains("/login"), "URL should contain /login after logout");

        driver.get(baseUrl + "/dashboard");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LoginPage redirected = new LoginPage(driver);
        redirected.waitUntilLoaded();
        Assert.assertTrue(redirected.isLoaded(), "Protected pages should not be accessible after logout");
    }

    @Test(description = "T077: Validate Session Handling")
    public void testSessionHandling() {
        String email = configReader.getProperty("username", "testuser@example.com");
        String password = configReader.getProperty("password", "P@ssw0rd");
        String baseUrl = configReader.getProperty("baseUrl", "http://localhost:5173");

        LoginUtils loginUtils = new LoginUtils(driver);
        HomePage home = loginUtils.loginWithValidCredentials(email, password);
        Assert.assertTrue(home.isLoaded(), "User should be logged in after login");

        Set<Cookie> cookies = driver.manage().getCookies();
        Assert.assertFalse(cookies.isEmpty(), "Session cookies should be present after login");

        driver.navigate().refresh();
        home = new HomePage(driver);
        home.waitUntilLoaded();
        Assert.assertTrue(home.isLoaded(), "Session should remain active after refresh");

        driver.switchTo().newWindow(WindowType.TAB);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        driver.get(baseUrl);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        HomePage homeNewTab = new HomePage(driver);
        homeNewTab.waitUntilLoaded();
        Assert.assertTrue(homeNewTab.isLoaded(), "Session should be maintained in a new tab");

        DriverFactory.quitDriver();

        driver = DriverFactory.createDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(configReader.getIntProperty("implicitWait", 10)));
        driver.get(baseUrl);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        WaitUtils waitUtils = new WaitUtils(driver);
        waitUtils.waitForPageLoad();

        String sessionPersistence = configReader.getProperty("sessionPersistence", "ephemeral");
        if ("persistent".equalsIgnoreCase(sessionPersistence)) {
            HomePage homeAfterRestart = new HomePage(driver);
            homeAfterRestart.waitUntilLoaded();
            Assert.assertTrue(homeAfterRestart.isLoaded(), "Session expected to persist after browser restart");
        } else {
            LoginPage loginAfterRestart = new LoginPage(driver);
            loginAfterRestart.waitUntilLoaded();
            Assert.assertTrue(loginAfterRestart.isLoaded(), "Session expected to expire after browser restart");
        }

        try {
            HomePage maybeHome = new HomePage(driver);
            if (maybeHome.isLoaded()) {
                loginUtils.logoutUser(maybeHome);
            }
        } catch (Exception ignored) {
        }

        driver.get(baseUrl + "/dashboard");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LoginPage afterLogoutRedirect = new LoginPage(driver);
        afterLogoutRedirect.waitUntilLoaded();
        Assert.assertTrue(afterLogoutRedirect.isLoaded(), "Accessing protected URL after logout should redirect to login page");
    }
}
