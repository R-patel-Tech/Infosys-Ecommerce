package pages;

import base.BasePage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OrderSummaryPage extends BasePage {
    private static final Pattern SUMMARY_LINE = Pattern.compile("^(\\d+)\\s*x\\s*(.+)$");

    private final By summaryShell = By.cssSelector(".checkout-summary");
    private final By summaryHeading = By.xpath("//aside[contains(@class,'checkout-summary')]//h2[normalize-space()='Cart summary']");
    private final By summaryItem = By.cssSelector(".checkout-summary-item");
    private final By totalQuantity = By.xpath("//aside[contains(@class,'checkout-summary')]//div[.//span[normalize-space()='Total quantity']]//strong");
    private final By totalAmount = By.xpath("//aside[contains(@class,'checkout-summary')]//div[.//span[normalize-space()='Total']]//strong");

    private final WebDriverWait explicitWait;

    public OrderSummaryPage(WebDriver driver) {
        super(driver);
        this.explicitWait = new WebDriverWait(driver, Duration.ofSeconds(configReader.getIntProperty("explicitWait", 15)));
    }

    public OrderSummaryPage waitUntilLoaded() {
        explicitWait.until(driver -> isDisplayed(summaryShell) && isDisplayed(summaryHeading));
        explicitWait.until(driver -> getSummaryItemCount() > 0 || isDisplayed(totalAmount));
        return this;
    }

    public boolean isDisplayed() {
        return isDisplayed(summaryShell) && isDisplayed(summaryHeading);
    }

    public int getSummaryItemCount() {
        return driver.findElements(summaryItem).size();
    }

    public List<SummaryItem> getSummaryItems() {
        List<SummaryItem> items = new ArrayList<>();
        for (WebElement item : driver.findElements(summaryItem)) {
            items.add(readSummaryItem(item));
        }
        return items;
    }

    public Optional<SummaryItem> findItemByName(String productName) {
        if (productName == null || productName.isBlank()) {
            return Optional.empty();
        }

        return getSummaryItems().stream().filter(item -> productName.trim().equals(item.getProductName())).findFirst();
    }

    public int getTotalQuantity() {
        return Integer.parseInt(textOf(totalQuantity));
    }

    public BigDecimal getDisplayedTotalAmount() {
        return parseMoney(textOf(totalAmount));
    }

    public Optional<BigDecimal> getOptionalChargeAmount(String label) {
        By row = By.xpath("//aside[contains(@class,'checkout-summary')]//div[.//span[normalize-space()='" + label + "']]//strong");
        java.time.Duration originalWait = java.time.Duration.ofSeconds(configReader.getIntProperty("implicitWait", 2));
        try {
            driver.manage().timeouts().implicitlyWait(java.time.Duration.ZERO);
            List<WebElement> elements = driver.findElements(row);
            if (elements.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(parseMoney(elements.get(0).getText()));
        } finally {
            driver.manage().timeouts().implicitlyWait(originalWait);
        }
    }

    public boolean isChargeDisplayed(String label) {
        By row = By.xpath("//aside[contains(@class,'checkout-summary')]//div[.//span[normalize-space()='" + label + "']]//strong");
        java.time.Duration originalWait = java.time.Duration.ofSeconds(configReader.getIntProperty("implicitWait", 2));
        try {
            driver.manage().timeouts().implicitlyWait(java.time.Duration.ZERO);
            List<WebElement> elements = driver.findElements(row);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } finally {
            driver.manage().timeouts().implicitlyWait(originalWait);
        }
    }

    public BigDecimal calculateExpectedTotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (SummaryItem item : getSummaryItems()) {
            subtotal = subtotal.add(item.getSubtotal());
        }

        BigDecimal tax = getOptionalChargeAmount("Tax").orElse(BigDecimal.ZERO);
        BigDecimal shipping = getOptionalChargeAmount("Shipping").orElse(BigDecimal.ZERO);
        return subtotal.add(tax).add(shipping).setScale(2, RoundingMode.HALF_UP);
    }

    private SummaryItem readSummaryItem(WebElement item) {
        List<WebElement> strongElements = item.findElements(By.tagName("strong"));
        if (strongElements.size() < 2) {
            throw new IllegalStateException("Unable to parse order summary item.");
        }

        String productName = strongElements.get(0).getText().trim();
        String subtotalText = strongElements.get(strongElements.size() - 1).getText().trim();
        String detailText = item.findElement(By.tagName("p")).getText().trim();

        Matcher matcher = SUMMARY_LINE.matcher(detailText);
        if (!matcher.matches()) {
            throw new IllegalStateException("Unable to read quantity from: " + detailText);
        }

        int quantity = Integer.parseInt(matcher.group(1));
        BigDecimal unitPrice = parseMoney(matcher.group(2));
        BigDecimal subtotal = parseMoney(subtotalText);
        return new SummaryItem(productName, quantity, unitPrice, subtotal);
    }

    private BigDecimal parseMoney(String value) {
        String sanitized = value == null ? "" : value.replaceAll("[^0-9.]", "");
        if (sanitized.isBlank()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(sanitized).setScale(2, RoundingMode.HALF_UP);
    }

    public static final class SummaryItem {
        private final String productName;
        private final int quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal subtotal;

        public SummaryItem(String productName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }
    }
}
