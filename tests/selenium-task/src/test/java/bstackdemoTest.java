import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;

import java.net.URL;
import java.net.MalformedURLException;

public class bstackdemoTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://bstackdemo.com/";

    private final By userField = By.id("username");
    private final By passField = By.id("password");
    private final By submitBtn = By.cssSelector("button[type='submit']");
    private final By alertBox = By.id("flash");
    private final By logoutBtn = By.cssSelector("a.button.secondary.radius");

    @Before
    public void init() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        driver = new RemoteWebDriver(new URL("http://selenium:4444/wd/hub"), options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, 10);
    }

    private WebElement find(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return driver.findElement(locator);
    }

    private void openLoginPage() {
        driver.get(BASE_URL);
    }

    private void performLogin(String username, String password) {
        find(userField).clear();
        find(userField).sendKeys(username);

        find(passField).clear();
        find(passField).sendKeys(password);

        find(submitBtn).click();
    }

    @Test
    public void shouldLoginSuccessfully() {
        openLoginPage();
        performLogin("tomsmith", "SuperSecretPassword!");

        String message = find(alertBox).getText().trim();
        System.out.println("Success message: " + message);

        Assert.assertTrue(message.contains("secure area"));
    }

   @Test
    public void shouldLogoutAfterLogin() {
        openLoginPage();
        performLogin("tomsmith", "SuperSecretPassword!");

        find(logoutBtn).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(userField));

        String message = find(alertBox).getText().trim();
        System.out.println("Logout message: " + message);

        Assert.assertTrue(message.contains("logged out"));
    }

    @Test
    public void shouldRejectInvalidLogin() {
        openLoginPage();
        performLogin("invalidUser", "invalidPass");

        String message = find(alertBox).getText().trim();
        System.out.println("Error message: " + message);

        Assert.assertTrue(message.contains("invalid"));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}