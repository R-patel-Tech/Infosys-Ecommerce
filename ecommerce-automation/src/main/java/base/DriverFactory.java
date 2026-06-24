package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.ConfigReader;

public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        if (DRIVER.get() != null) {
            return DRIVER.get();
        }

        String browser = ConfigReader.getInstance().getProperty("browser", "chrome");
        if (!"chrome".equalsIgnoreCase(browser)) {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        if (!resolveCachedChromeDriver().isPresent()) {
            WebDriverManager.chromedriver().setup();
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1440,1200");
        }
        DRIVER.set(new ChromeDriver(options));
        return DRIVER.get();
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static Optional<String> resolveCachedChromeDriver() {
        String configuredDriver = System.getProperty("webdriver.chrome.driver");
        if (configuredDriver != null && !configuredDriver.isBlank()) {
            return Optional.of(configuredDriver);
        }

        Path cacheRoot = Paths.get(System.getProperty("user.home"), ".cache", "selenium", "chromedriver", "win64");
        if (!Files.isDirectory(cacheRoot)) {
            return Optional.empty();
        }

        try {
            return Files.list(cacheRoot)
                    .filter(Files::isDirectory)
                    .sorted(Comparator.comparing(Path::getFileName).reversed())
                    .map(path -> path.resolve("chromedriver.exe"))
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .map(path -> {
                        System.setProperty("webdriver.chrome.driver", path.toString());
                        return path.toString();
                    });
        } catch (IOException exception) {
            return Optional.empty();
        }
    }
}
