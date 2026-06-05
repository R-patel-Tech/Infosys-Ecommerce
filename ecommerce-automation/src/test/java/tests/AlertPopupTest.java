package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.PopupPage;
import utils.AlertUtils;

public class AlertPopupTest extends BaseTest {

    @Test(description = "Handle JavaScript alert, confirmation alert, prompt alert, browser notification popup, and modal dialog")
    public void handleAlertsAndPopups() {
        PopupPage popupPage = new PopupPage(driver, wait).openDemoPage();
        AlertUtils alertUtils = new AlertUtils(driver, java.time.Duration.ofSeconds(configReader.getIntProperty("explicitWait", 15)));

        Assert.assertTrue(popupPage.isLoaded(), "Popup demo page did not load.");

        popupPage.clickJavaScriptAlertButton();
        alertUtils.waitForAlert();
        Assert.assertEquals(alertUtils.getAlertText(), "JavaScript alert triggered successfully");
        alertUtils.acceptAlert();

        popupPage.clickConfirmationAlertButton();
        alertUtils.waitForAlert();
        Assert.assertEquals(alertUtils.getAlertText(), "Do you want to continue?");
        alertUtils.dismissAlert();
        Assert.assertEquals(popupPage.getModalResult(), "Confirmation dismissed");

        popupPage.clickPromptAlertButton();
        alertUtils.waitForAlert();
        Assert.assertEquals(alertUtils.getAlertText(), "Please enter your name");
        alertUtils.sendTextToAlert("Codex");
        alertUtils.acceptAlert();
        Assert.assertEquals(popupPage.getModalResult(), "Prompt value: Codex");

        popupPage.openModal();
        Assert.assertTrue(alertUtils.waitForModalVisible(org.openqa.selenium.By.id("modal")), "Modal was not displayed.");
        Assert.assertTrue(popupPage.isModalVisible(), "Modal dialog did not open.");
        popupPage.closeModal();
        Assert.assertEquals(popupPage.getModalResult(), "Modal closed");

        popupPage.clickBrowserNotificationButton();
        Assert.assertTrue(popupPage.getNotificationResult().contains("Browser notification popup handled"),
                "Browser notification popup was not handled.");
    }
}
