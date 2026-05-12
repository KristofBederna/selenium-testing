import java.io.File;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

//BasePage class
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    private By footer = By.id("custom-footer");

    private static final String BASE_URL = ConfigReader.get("base.url");

    public BasePage() {
    }

    public BasePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    protected void openPage() {
        driver.get(BASE_URL);
    }

    protected WebElement find(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return driver.findElement(locator);
    }

    protected boolean isElementPresent(By locator) {
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return driver.findElements(locator).size() > 0;
    }

    protected void selectDropdownOptionUsername(String username) {
        find(By.id("username")).click();
        By optionLocator = By.xpath(
                "//div[contains(@class,'option') and normalize-space(text())='" + username + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));
        driver.findElement(optionLocator).click();
    }

    protected void selectDropdownOptionPassword(String password) {
        find(By.id("password")).click();
        By optionLocator = By.xpath(
                "//div[contains(@class,'option') and normalize-space(text())='" + password + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));
        driver.findElement(optionLocator).click();
    }

    public boolean waitForFile(String downloadPath, int timeoutSec) {
        for (int i = 0; i < timeoutSec; i++) {
            File dir = new File(downloadPath);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".pdf") && !name.endsWith(".crdownload"));
            if (files != null && files.length > 0)
                return true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
    }

    public WebElement getFooterElement() {
        return find(footer);
    }
}