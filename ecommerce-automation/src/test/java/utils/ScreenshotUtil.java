package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public final class ScreenshotUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {
    }

    public static String captureScreenshot(WebDriver driver, String fileName) {
        if (!(driver instanceof TakesScreenshot takesScreenshot)) {
            throw new IllegalArgumentException("Current driver does not support screenshots.");
        }

        ConfigReader configReader = ConfigReader.getInstance();
        String screenshotPath = configReader.getProperty("screenshot.path", "src/test/reports/screenshots");
        Path directory = Paths.get(screenshotPath).toAbsolutePath().normalize();
        String safeFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_") + "_" + LocalDateTime.now().format(FORMATTER) + ".png";
        Path targetFile = directory.resolve(safeFileName);

        try {
            Files.createDirectories(directory);
            Files.write(targetFile, takesScreenshot.getScreenshotAs(OutputType.BYTES));
            return targetFile.toString();
        } catch (IOException ioException) {
            throw new IllegalStateException("Unable to save screenshot to " + targetFile, ioException);
        }
    }
}
