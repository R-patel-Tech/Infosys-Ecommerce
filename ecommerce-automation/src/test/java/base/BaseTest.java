package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import reporting.ExtentManager;
import utils.ConfigReader;

public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final ConfigReader configReader = ConfigReader.getInstance();
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        ExtentManager.getInstance();
        logger.info("Test suite initialization completed.");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpBrowser() {
        String browser = configReader.getProperty("browser", "chrome").toLowerCase(Locale.ROOT);
        boolean headless = configReader.getBooleanProperty("headless", false);
        int pageLoadTimeout = configReader.getIntProperty("pageLoadTimeout", 30);
        int explicitWait = configReader.getIntProperty("explicitWait", 15);

        switch (browser) {
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                Map<String, Object> chromePrefs = new HashMap<>();
                chromePrefs.put("profile.default_content_setting_values.notifications", 2);
                chromeOptions.setExperimentalOption("prefs", chromePrefs);
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--start-maximized");
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(configReader.getIntProperty("implicitWait", 10)));
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));

        String baseUrl = configReader.getProperty("baseUrl", "http://localhost:5173");
        driver.get(baseUrl);
        logger.info("Opened application URL: {}", baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentManager.flush();
        logger.info("Test suite execution completed.");
    }
}
