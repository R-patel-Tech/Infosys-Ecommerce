package pages;

import base.BasePage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PopupPage extends BasePage {
    private static final String DEMO_HTML = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0" />
              <title>Alert and Popup Demo</title>
              <style>
                body { font-family: Arial, sans-serif; padding: 24px; }
                .toolbar { display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 20px; }
                button { padding: 10px 14px; cursor: pointer; }
                .modal-backdrop { display: none; position: fixed; inset: 0; background: rgba(0,0,0,0.5); align-items: center; justify-content: center; }
                .modal-backdrop.open { display: flex; }
                .modal-card { background: #fff; border-radius: 12px; padding: 20px; min-width: 280px; box-shadow: 0 12px 40px rgba(0,0,0,0.25); }
                .modal-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 16px; }
                #modal-result, #notification-result { margin-top: 16px; font-weight: 600; }
              </style>
            </head>
            <body>
              <h1>Alert Demo</h1>
              <div class="toolbar">
                <button id="js-alert">JavaScript Alert</button>
                <button id="confirm-alert">Confirmation Alert</button>
                <button id="prompt-alert">Prompt Alert</button>
                <button id="open-modal">Open Modal</button>
                <button id="browser-notification">Browser Notification Popup</button>
              </div>
              <div id="modal" class="modal-backdrop" role="dialog" aria-modal="true" aria-label="Demo Modal">
                <div class="modal-card">
                  <h2>Modal Dialog</h2>
                  <p id="modal-message">This is a modal dialog popup.</p>
                  <div class="modal-actions">
                    <button id="close-modal">Close</button>
                  </div>
                </div>
              </div>
              <div id="modal-result"></div>
              <div id="notification-result"></div>
              <script>
                document.getElementById('js-alert').addEventListener('click', () => {
                  alert('JavaScript alert triggered successfully');
                });
                document.getElementById('confirm-alert').addEventListener('click', () => {
                  const confirmed = confirm('Do you want to continue?');
                  document.getElementById('modal-result').textContent = confirmed ? 'Confirmation accepted' : 'Confirmation dismissed';
                });
                document.getElementById('prompt-alert').addEventListener('click', () => {
                  const value = prompt('Please enter your name', 'Guest');
                  document.getElementById('modal-result').textContent = value ? `Prompt value: ${value}` : 'Prompt cancelled';
                });
                document.getElementById('open-modal').addEventListener('click', () => {
                  document.getElementById('modal').classList.add('open');
                });
                document.getElementById('close-modal').addEventListener('click', () => {
                  document.getElementById('modal').classList.remove('open');
                  document.getElementById('modal-result').textContent = 'Modal closed';
                });
                document.getElementById('browser-notification').addEventListener('click', () => {
                  document.getElementById('notification-result').textContent = 'Browser notification popup handled';
                });
              </script>
            </body>
            </html>
            """;

    private final By jsAlertButton = By.id("js-alert");
    private final By confirmAlertButton = By.id("confirm-alert");
    private final By promptAlertButton = By.id("prompt-alert");
    private final By modalButton = By.id("open-modal");
    private final By modalRoot = By.id("modal");
    private final By modalCloseButton = By.id("close-modal");
    private final By modalResult = By.id("modal-result");
    private final By notificationResult = By.id("notification-result");
    private final By browserNotificationButton = By.id("browser-notification");
    private final By toolbar = By.cssSelector(".toolbar");

    public PopupPage(WebDriver driver) {
        super(driver);
    }

    public PopupPage openDemoPage() {
        String encodedHtml = Base64.getEncoder().encodeToString(DEMO_HTML.getBytes(StandardCharsets.UTF_8));
        driver.get("data:text/html;charset=utf-8;base64," + encodedHtml);
        waitUtils.waitForPageLoad();
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(toolbar) && isDisplayed(jsAlertButton);
    }

    public void clickJavaScriptAlertButton() {
        click(jsAlertButton);
    }

    public void clickConfirmationAlertButton() {
        click(confirmAlertButton);
    }

    public void clickPromptAlertButton() {
        click(promptAlertButton);
    }

    public void openModal() {
        click(modalButton);
    }

    public void closeModal() {
        click(modalCloseButton);
    }

    public boolean isModalVisible() {
        return isDisplayed(modalRoot);
    }

    public String getModalResult() {
        return isDisplayed(modalResult) ? textOf(modalResult) : "";
    }

    public String getNotificationResult() {
        return isDisplayed(notificationResult) ? textOf(notificationResult) : "";
    }

    public void clickBrowserNotificationButton() {
        click(browserNotificationButton);
    }
}
