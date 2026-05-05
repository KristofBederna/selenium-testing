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

    private final By signInLink = By.id("signin");
    private final By loginButton = By.id("login-btn");
    private final By loggedInName = By.className("username");

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
        find(signInLink).click();
        selectDropdownOptionUsername(username);
        selectDropdownOptionPassword(password);
        find(loginButton).click();
    }

    private void selectDropdownOptionUsername(String username) {
        find(By.id("username")).click();
        By optionLocator = By.xpath(
                "//div[contains(@class,'option') and normalize-space(text())='" + username + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));
        driver.findElement(optionLocator).click();
    }

    private void selectDropdownOptionPassword(String password) {
        find(By.id("password")).click();
        By optionLocator = By.xpath(
                "//div[contains(@class,'option') and normalize-space(text())='" + password + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));
        driver.findElement(optionLocator).click();
    }

    private void performLogout() {
        find(signInLink).click();
    }

    /*
     * Login test
     * Explicit wait
     * Select dropdown options for username and password
     * Fill input (select) x2 (counts as 1 type)
     * Submit a form
     * Complex XPath for dropdown options location x2
     */
    @Test
    public void LoginSuccessful() {
        openLoginPage();
        performLogin("demouser", "testingisfun99");

        wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInName));

        String loggedInUser = find(loggedInName).getText().trim();

        Assert.assertEquals("demouser", loggedInUser);
    }

    private boolean isElementPresent(By locator) {
        return driver.findElements(locator).size() > 0;
    }

    /*
     * Logout test
     * Explicit wait
     */
    @Test
    public void LogoutSuccessful() {
        openLoginPage();
        performLogin("demouser", "testingisfun99");
        wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInName));
        performLogout();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(loggedInName));

        Assert.assertFalse(isElementPresent(loggedInName));
    }

    private final By firstItemButton = By.className("shelf-item__buy-btn");
    private final By checkoutButton = By.className("buy-btn");
    private final By firstNameInput = By.id("firstNameInput");
    private final By lastNameInput = By.id("lastNameInput");
    private final By addressLineInput = By.id("addressLine1Input");
    private final By provinceInput = By.id("provinceInput");
    private final By postalCodeInput = By.id("postCodeInput");
    private final By shippingSubmitButton = By.id("checkout-shipping-continue");
    private final By continueShoppingButton = By.className("optimizedCheckout-buttonSecondary");
    private final By ordersLink = By.id("orders");
    private final By ordersListing = By.className("orders-listing");

    private void enterCheckoutDetails(String firstName, String lastName, String address, String province,
            String postalCode) {
        find(firstNameInput).sendKeys(firstName);
        find(lastNameInput).sendKeys(lastName);
        find(addressLineInput).sendKeys(address);
        find(provinceInput).sendKeys(province);
        find(postalCodeInput).sendKeys(postalCode);
    }

    /*
     * Submit a form x1
     * Fill input (text) x5 (counts as 1 type)
     * Submit a form after login
     * Explicit wait
     */
    @Test
    public void OrderFirstItem() {
        openLoginPage();
        performLogin("demouser", "testingisfun99");
        wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInName));

        find(firstItemButton).click();

        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));
        find(checkoutButton).click();

        wait.until(ExpectedConditions.urlContains("checkout"));

        enterCheckoutDetails("John", "Doe", "123 Main St", "California", "12345");

        find(shippingSubmitButton).click();

        wait.until(ExpectedConditions.urlContains("confirmation"));
        find(continueShoppingButton).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(ordersLink));
        find(ordersLink).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(ordersListing));

        Assert.assertTrue(isElementPresent(ordersListing));
    }

    // Page title test
    @Test
    public void VerifyPageTitle() {
        openLoginPage();
        String expectedTitle = "StackDemo";
        String actualTitle = driver.getTitle();

        Assert.assertEquals(expectedTitle, actualTitle);
    }

    // Static page test
    @Test
    public void VerifyElementVisibility() {
        openLoginPage();
        Assert.assertTrue(isElementPresent(signInLink));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}