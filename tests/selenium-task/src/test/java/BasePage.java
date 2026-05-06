import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

//BasePage class
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    private static final String BASE_URL = "https://bstackdemo.com/";

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
}