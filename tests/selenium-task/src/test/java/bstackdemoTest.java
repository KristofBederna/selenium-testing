import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
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
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.default_directory", downloadPath);
        prefs.put("download.directory_upgrade", true);
        prefs.put("plugins.always_open_pdf_externally", true);
        prefs.put("safebrowsing.enabled", false);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        // options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        options.setExperimentalOption("prefs", prefs);

        driver = new RemoteWebDriver(
                new URL(ConfigReader.get("grid.url")),
                options);

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
     * Download a file and verify its existence
     */
    @Test
    public void CompleteOrderProcessVerification() {
        openPage();

        HomePage home = new HomePage(driver, wait);

        home = home.goToSignIn()
                .login(ConfigReader.get("username"), ConfigReader.get("password"));

        home.addFirstItemToCart();

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

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement downloadBtn = find(confirmation.getDownloadPDF());
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", downloadBtn);

        boolean isDownloaded = waitForFile(ConfigReader.get("download.path"), 15);
        Assert.assertTrue(isDownloaded);

        File dir = new File(ConfigReader.get("download.path"));
        File[] files = dir.listFiles((d, name) -> name.endsWith(".pdf"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }

        home = confirmation.continueShopping();
        OrdersPage orders = home.goToOrders();

        Assert.assertTrue(orders.hasOrders());
    }

    /*
     * Complex XPath x1
     * Radio button or checkbox test (favorite button is a toggle button)
     */
    @Test
    public void VerifyAddToFavorites() {
        openPage();

        HomePage home = new HomePage(driver, wait);

        home = home.goToSignIn()
                .login(ConfigReader.get("username"), ConfigReader.get("password"));

        home.addFirstItemToFavorites();

        FavoritesPage favorites = home.goToFavorites();

        wait.until(ExpectedConditions.urlContains("favourites"));

        Assert.assertTrue(favorites.hasFavorites());
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
    public void VerifyVisibilityOfSignInLinkOnHomePage() {
        openPage();
        HomePage home = new HomePage(driver, wait);
        Assert.assertTrue(isElementPresent(home.getSignInLink()));
    }

    // History test: navigate to orders, then back and verify URL (orders page
    // requires login, so it should redirect to sign in page)
    @Test
    public void GoToLoginAndNavigateBackVerifyURL() {
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

    /*
     * Cookie manipulation test
     * JavaScript executor test
     */
    @Test
    public void SaveUserNameToCookiesAndReloadThenDeleteCookies() {
        openPage();

        Cookie testCookie = new Cookie.Builder("test-user", "demouser")
                .domain("bstackdemo.com")
                .path("/")
                .build();
        driver.manage().addCookie(testCookie);

        Cookie retrieved = driver.manage().getCookieNamed("test-user");
        Assert.assertNotNull("Cookie was not set", retrieved);
        Assert.assertEquals("demouser", retrieved.getValue());

        ((JavascriptExecutor) driver).executeScript(
                "sessionStorage.setItem('username', arguments[0]);", retrieved.getValue());

        driver.navigate().refresh();

        HomePage home = new HomePage(driver, wait);
        String loggedInUser = home.getLoggedInUser();
        Assert.assertEquals(retrieved.getValue(), loggedInUser);

        driver.manage().deleteCookieNamed("test-user");
        Assert.assertNull(driver.manage().getCookieNamed("test-user"));
    }

    /*
     * Complex XPath x1
     */
    @Test
    public void VerifyGeolocationIsNeededForOffers() {
        openPage();

        HomePage home = new HomePage(driver, wait);
        home.goToSignIn().login(ConfigReader.get("username"), ConfigReader.get("password"));

        OffersPage offers = home.goToOffers();
        wait.until(ExpectedConditions.urlContains("offers"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                offers.getLocationBasedOffer()));

        String offerText = offers.getLocationBasedOfferText();

        Assert.assertTrue(
                offerText.equals("Sorry we do not have any promotional offers in your city.")
                        || offerText.equals("We've promotional offers in your location."));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}