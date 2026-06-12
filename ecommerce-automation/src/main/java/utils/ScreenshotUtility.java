package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public final class ScreenshotUtility {
    private ScreenshotUtility() {
    }

    public static String captureScreenshot(WebDriver driver, String methodName) {
        if (!(driver instanceof TakesScreenshot)) {
            return null;
        }

        Path screenshotFolder = Paths.get("target", "screenshots");
        try {
            Files.createDirectories(screenshotFolder);
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Path screenshotPath = screenshotFolder.resolve(methodName + "_" + System.currentTimeMillis() + ".png");
            Files.write(screenshotPath, screenshotBytes);
            return screenshotPath.toAbsolutePath().toString();
        } catch (IOException ignored) {
            return null;
        }
    }
}
