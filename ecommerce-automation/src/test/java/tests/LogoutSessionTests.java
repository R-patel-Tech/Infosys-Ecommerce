package tests;

import base.BaseTest;
import base.DriverFactory;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WindowType;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;
import utils.WaitUtils;

import java.time.Duration;
import java.util.Set;

public class LogoutSessionTests extends BaseTest {

    @Test(description = "T076: Automate Logout Functionality")
    public void testLogoutFunctionality() {
        String email = configReader.getProperty("username", "testuser@example.com");
        String password = configReader.getProperty("password", "P@ssw0rd");
        String baseUrl = configReader.getProperty("baseUrl", "http://localhost:5173");

        LoginPage loginPage = new LoginPage(driver).open();
        HomePage home = loginPage.loginToDashboard(email, password);

        Assert.assertTrue(home.isLoaded(), "User should be logged in and home should be loaded");

        // Perform logout via page object
        LoginPage afterLogout = home.logout();

        Assert.assertTrue(afterLogout.isLoaded(), "Login page should be displayed after logout");
        Assert.assertTrue(afterLogout.getCurrentUrl().contains("/login"), "URL should contain /login after logout");

        // Attempt to access protected page after logout
        driver.get(baseUrl + "/dashboard");
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
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

        // 1. Login
        LoginPage loginPage = new LoginPage(driver).open();
        HomePage home = loginPage.loginToDashboard(email, password);
        Assert.assertTrue(home.isLoaded(), "User should be logged in after login");

        // 2. Capture current session state (cookies)
        Set<Cookie> cookies = driver.manage().getCookies();
        Assert.assertFalse(cookies.isEmpty(), "Session cookies should be present after login");

        // 3. Refresh browser and verify session remains active
        driver.navigate().refresh();
        home = new HomePage(driver);
        home.waitUntilLoaded();
        Assert.assertTrue(home.isLoaded(), "Session should remain active after refresh");

        // 4. Open a new tab and verify session is maintained
        driver.switchTo().newWindow(WindowType.TAB);
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        driver.get(baseUrl);
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        HomePage homeNewTab = new HomePage(driver);
        homeNewTab.waitUntilLoaded();
        Assert.assertTrue(homeNewTab.isLoaded(), "Session should be maintained in a new tab");

        // 5. Close browser and reopen application (simulate restart)
        DriverFactory.quitDriver();

        // Recreate driver within the same test to verify persistence
        driver = DriverFactory.createDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(configReader.getIntProperty("implicitWait", 10)));
        driver.get(baseUrl);
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        WaitUtils waitUtils = new WaitUtils(driver);
        waitUtils.waitForPageLoad();

        // 6. Verify session behavior according to configuration
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

        // 7. Attempt to access protected URL after logout and verify redirect to login
        // If currently logged in, perform logout first
        try {
            HomePage maybeHome = new HomePage(driver);
            if (maybeHome.isLoaded()) {
                maybeHome.logout();
            }
        } catch (Exception ignored) {
        }

        driver.get(baseUrl + "/dashboard");
        // Wait for 2 seconds to observe execution
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LoginPage afterLogoutRedirect = new LoginPage(driver);
        afterLogoutRedirect.waitUntilLoaded();
        Assert.assertTrue(afterLogoutRedirect.isLoaded(), "Accessing protected URL after logout should redirect to login page");
    }
}
