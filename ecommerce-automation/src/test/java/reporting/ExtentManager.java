package reporting;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import utils.ConfigReader;

public final class ExtentManager {
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            ConfigReader configReader = ConfigReader.getInstance();
            String reportPath = configReader.getProperty("report.path", "src/test/reports/ExtentReport.html");
            Path reportFile = Paths.get(reportPath).toAbsolutePath().normalize();

            try {
                Files.createDirectories(reportFile.getParent());
            } catch (Exception ignored) {
                // Directory creation is best-effort.
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFile.toString());
            sparkReporter.config().setReportName(configReader.getProperty("report.name", "Ecommerce Automation Report"));
            sparkReporter.config().setDocumentTitle("Ecommerce Automation");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Project", "ecommerce-automation");
            extentReports.setSystemInfo("Environment", System.getProperty("env", "local"));
            extentReports.setSystemInfo("Browser", configReader.getProperty("browser", "chrome"));
            extentReports.setSystemInfo("Base URL", configReader.getProperty("baseUrl", "http://localhost:5173"));
        }

        return extentReports;
    }

    public static ExtentTest startTest(String testName) {
        ExtentTest extentTest = getInstance().createTest(testName);
        CURRENT_TEST.set(extentTest);
        return extentTest;
    }

    public static ExtentTest getTest() {
        return CURRENT_TEST.get();
    }

    public static void clearTest() {
        CURRENT_TEST.remove();
    }

    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
