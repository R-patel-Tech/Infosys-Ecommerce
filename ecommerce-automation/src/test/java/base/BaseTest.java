package base;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;

public abstract class BaseTest {
    protected WebDriver driver;
    protected final ConfigReader configReader = ConfigReader.getInstance();

    @BeforeMethod(alwaysRun = true)
    public void setUpBrowser() {
        driver = DriverFactory.createDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(configReader.getIntProperty("implicitWait", 10)));
        driver.get(configReader.getProperty("baseUrl", "http://localhost:5173"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownBrowser() {
        DriverFactory.quitDriver();
    }
}
