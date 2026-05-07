import org.junit.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.net.MalformedURLException;

public class bstackdemoTest extends BasePage {
    @Before
    public void init() throws MalformedURLException {
        String downloadPath = "/tmp/downloads";
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

        driver = new RemoteWebDriver(new URL("http://selenium:4444/wd/hub"), options);
        wait = new WebDriverWait(driver, 10);
    }

    /*
     * * Login test * Explicit wait * Select dropdown options for username and
     * password * Fill input (select) x2 (counts as 1 type) * Submit a form *
     * Complex XPath for dropdown options location x2
     */ @Test
    public void LoginSuccessful() {
        openPage();
        HomePage home = new HomePage(driver, wait);
        SignInPage signInPage = home.goToSignIn();
        signInPage.login("demouser", "testingisfun99");
        String loggedInUser = home.getLoggedInUser();
        Assert.assertEquals("demouser", loggedInUser);
    }

    /* * Logout test * Explicit wait */
    @Test
    public void LogoutSuccessful() {
        openPage();
        HomePage home = new HomePage(driver, wait);
        SignInPage signInPage = home.goToSignIn();
        signInPage.login("demouser", "testingisfun99");
        home.performLogout();
        Assert.assertTrue(find(home.getSignInLink()).getText().trim().equals("Sign In"));
    }

    /*
     * Submit a form x1
     * Fill input (text) x5 (counts as 1 type)
     * Submit a form after login
     * Explicit wait
     */
    @Test
    public void OrderFirstItem() {
        openPage();

        HomePage home = new HomePage(driver, wait);

        home = home.goToSignIn()
                .login("demouser", "testingisfun99");

        home.addFirstItemToCart();

        CheckoutPage checkout = home.goToCheckout();
        ConfirmationPage confirmation = checkout.fillForm(
                "John", "Doe", "123 Main St", "California", "12345");

        home = confirmation.continueShopping();
        OrdersPage orders = home.goToOrders();

        Assert.assertTrue(orders.hasOrders());
    }

    // Page title test
    @Test
    public void VerifyPageTitle() {
        openPage();
        String expectedTitle = "StackDemo";
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

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}