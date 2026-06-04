package listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import java.lang.reflect.Field;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import reporting.ExtentManager;
import utils.ScreenshotUtil;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        ExtentManager.startTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().pass("Test passed");
        }
        ExtentManager.clearTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String screenshotPath = captureScreenshot(result);
        if (ExtentManager.getTest() == null) {
            ExtentManager.startTest(result.getMethod().getMethodName());
        }

        if (screenshotPath != null) {
            ExtentManager.getTest()
                    .fail(result.getThrowable(),
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        } else {
            ExtentManager.getTest().fail(result.getThrowable());
        }
        ExtentManager.clearTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (ExtentManager.getTest() == null) {
            ExtentManager.startTest(result.getMethod().getMethodName());
        }
        ExtentManager.getTest().skip(result.getThrowable());
        ExtentManager.clearTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    private String captureScreenshot(ITestResult result) {
        Object testInstance = result.getInstance();
        if (testInstance == null) {
            return null;
        }

        try {
            Field driverField = testInstance.getClass().getSuperclass().getDeclaredField("driver");
            driverField.setAccessible(true);
            Object value = driverField.get(testInstance);
            if (value instanceof WebDriver driver) {
                return ScreenshotUtil.captureScreenshot(driver, result.getMethod().getMethodName());
            }
        } catch (ReflectiveOperationException ignored) {
            // Screenshot capture is optional for listener reporting.
        }

        return null;
    }
}
