import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.net.URL;
import java.net.MalformedURLException;

public class bstackdemoTest extends BasePage {
    @Before
    public void init() throws MalformedURLException {
        String downloadPath = ConfigReader.get("download.path");
        new File(downloadPath).mkdirs();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath);
        prefs.put("download.prompt_for_download", false);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        // options.addArguments("--headless=new"); // headless_execution
        // (TODO: enable when headless mode when noVCN is no longer needed)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setExperimentalOption("prefs", prefs); // needed for downloads

        driver = new RemoteWebDriver(new URL(ConfigReader.get("grid.url")), options);
        wait = new WebDriverWait(driver, 10);
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
        openPage();
        HomePage home = new HomePage(driver, wait);
        SignInPage signInPage = home.goToSignIn();
        signInPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        String loggedInUser = home.getLoggedInUser();
        Assert.assertEquals(ConfigReader.get("username"), loggedInUser);
    }

    /*
     * Logout test
     * Explicit wait
     */
    @Test
    public void LogoutSuccessful() {
        openPage();
        HomePage home = new HomePage(driver, wait);
        SignInPage signInPage = home.goToSignIn();
        signInPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        home.performLogout();
        Assert.assertTrue(find(home.getSignInLink()).getText().trim().equals("Sign In"));
    }

    /*
     * Submit a form x1
     * Fill input (text) x5 (counts as 1 type)
     * Submit a form after login
     * Explicit wait
     * Random data
     */
    @Test
    public void OrderFirstItem() {
        openPage();

        HomePage home = new HomePage(driver, wait);

        home = home.goToSignIn()
                .login(ConfigReader.get("username"), ConfigReader.get("password"));

        home.addFirstItemToCart();

        // Generate random checkout data
        String randomFirst = "User" + UUID.randomUUID().toString().substring(0, 5);
        String randomLast = "Test" + UUID.randomUUID().toString().substring(0, 5);
        String randomAddress = UUID.randomUUID().toString().substring(0, 8) + " Main St";
        String randomProvince = "Province" + UUID.randomUUID().toString().substring(0, 5);
        String randomPostal = String.valueOf((int) (Math.random() * 90000) + 10000);

        CheckoutPage checkout = home.goToCheckout();
        ConfirmationPage confirmation = checkout.fillForm(
                randomFirst, randomLast,
                randomAddress, randomProvince,
                randomPostal);

        home = confirmation.continueShopping();
        OrdersPage orders = home.goToOrders();

        Assert.assertTrue(orders.hasOrders());
    }

    // Page title test
    @Test
    public void VerifyPageTitle() {
        openPage();
        String expectedTitle = ConfigReader.get("page.title");
        String actualTitle = driver.getTitle();

        Assert.assertEquals(expectedTitle, actualTitle);
    }

    // Static page test
    @Test
    public void VerifyElementVisibility() {
        openPage();
        HomePage home = new HomePage(driver, wait);
        Assert.assertTrue(isElementPresent(home.getSignInLink()));
    }

    // History test: navigate to orders, then back and verify URL (orders page
    // requires login, so it should redirect to sign in page)
    @Test
    public void GoToLoginAndNavigateBack() {
        openPage();

        HomePage home = new HomePage(driver, wait);

        home.goToOrders();

        wait.until(ExpectedConditions.urlToBe(ConfigReader.get("signin.url")));
        Assert.assertTrue(ConfigReader.get("signin.url").equals(driver.getCurrentUrl()));
        driver.navigate().back();

        wait.until(ExpectedConditions.urlToBe(ConfigReader.get("base.url")));
        Assert.assertTrue(ConfigReader.get("base.url").equals(driver.getCurrentUrl()));
    }

    // JavaScript executor test
    @Test
    public void ScrollToFooterOnEmptyFavoritesAndVerifyItsVisibility() {
        openPage();
        HomePage home = new HomePage(driver, wait);
        home.goToSignIn().login(ConfigReader.get("username"), ConfigReader.get("password"));

        FavoritesPage favorites = home.goToFavorites();

        wait.until(ExpectedConditions.urlContains("favourites"));

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");

        WebElement footerElement = favorites.getFooterElement();
        boolean isVisible = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var rect = arguments[0].getBoundingClientRect();" +
                        "return rect.top >= 0 && rect.bottom <= window.innerHeight;",
                footerElement);

        Assert.assertTrue(isVisible);
    }

    /*
     * Hover test
     * Complex XPath x1
     */
    @Test
    public void HoverChangesAddToCartButtonColor() {
        openPage();

        WebElement shelfItem = find(By.xpath("//div[@class='shelf-item' and @data-sku]"));
        WebElement addToCartBtn = shelfItem.findElement(By.className("shelf-item__buy-btn"));

        String colorBefore = addToCartBtn.getCssValue("background-color");

        new Actions(driver).moveToElement(shelfItem).perform();

        String colorAfter = addToCartBtn.getCssValue("background-color");

        Assert.assertNotEquals(colorBefore, colorAfter);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}