package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductDetailsPage extends BasePage {
    private final By heading = By.xpath("//section[contains(@class,'product-details-card')]//h1");
    private final By productImage = By.cssSelector(".product-image-large");
    private final By productPrice = By.cssSelector(".product-price-large");
    private final By backButton = By.xpath("//button[normalize-space()='Back']");

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    public ProductDetailsPage waitUntilLoaded() {
        waitUtils.waitForVisibility(heading);
        waitUtils.waitForVisibility(productImage);
        waitUtils.waitForVisibility(productPrice);
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(heading) && isDisplayed(productImage) && isDisplayed(productPrice);
    }

    public String getProductTitle() {
        return textOf(heading);
    }

    public String getProductPrice() {
        return textOf(productPrice);
    }

    public boolean isProductImageDisplayed() {
        return isDisplayed(productImage);
    }

    public String getProductImageSource() {
        WebElement image = visible(productImage);
        return image.getAttribute("src");
    }

    public ProductListingPage clickBack() {
        click(backButton);
        waitUtils.waitForUrlContains("/products");
        return new ProductListingPage(driver);
    }
}
