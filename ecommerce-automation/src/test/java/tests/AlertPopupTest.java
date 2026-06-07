package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.PopupPage;
import utils.AlertUtils;

public class AlertPopupTest extends BaseTest {
    @Test
    public void handleAlertsAndPopups() {
        PopupPage popupPage = new PopupPage(driver).openDemoPage();
        AlertUtils alertUtils = new AlertUtils(driver);

        Assert.assertTrue(popupPage.isLoaded(), "Popup demo page did not load.");

        popupPage.clickJavaScriptAlertButton();
        Assert.assertEquals(alertUtils.getAlertText(), "JavaScript alert triggered successfully");
        alertUtils.acceptAlert();

        popupPage.clickConfirmationAlertButton();
        Assert.assertEquals(alertUtils.getAlertText(), "Do you want to continue?");
        alertUtils.dismissAlert();
        Assert.assertEquals(popupPage.getModalResult(), "Confirmation dismissed");

        popupPage.clickPromptAlertButton();
        Assert.assertEquals(alertUtils.getAlertText(), "Please enter your name");
        alertUtils.enterTextInAlert("Codex");
        alertUtils.acceptAlert();
        Assert.assertEquals(popupPage.getModalResult(), "Prompt value: Codex");

        popupPage.openModal();
        Assert.assertTrue(driver.findElement(By.id("modal")).isDisplayed(), "Modal was not displayed.");
        Assert.assertTrue(popupPage.isModalVisible(), "Modal dialog did not open.");
        popupPage.closeModal();
        Assert.assertEquals(popupPage.getModalResult(), "Modal closed");

        popupPage.clickBrowserNotificationButton();
        Assert.assertTrue(popupPage.getNotificationResult().contains("Browser notification popup handled"));
    }
}
