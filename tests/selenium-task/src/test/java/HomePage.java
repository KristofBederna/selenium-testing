import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage extends BasePage {

    private By signInLink = By.id("signin");
    private By firstItemButton = By.className("shelf-item__buy-btn");
    private By checkoutButton = By.className("buy-btn");
    private By ordersLink = By.id("orders");
    private final By loggedInName = By.className("username");
    private By favoritesLink = By.id("favourites");

    public HomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public String getLoggedInUser() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInName));
        return find(loggedInName).getText().trim();
    }

    public SignInPage goToSignIn() {
        find(signInLink).click();
        return new SignInPage(driver, wait);
    }

    public void performLogout() {
        find(signInLink).click();
    }

    public void addFirstItemToCart() {
        find(firstItemButton).click();
    }

    public CheckoutPage goToCheckout() {
        find(checkoutButton).click();
        return new CheckoutPage(driver, wait);
    }

    public OrdersPage goToOrders() {
        find(ordersLink).click();
        return new OrdersPage(driver, wait);
    }

    public By getSignInLink() {
        return signInLink;
    }

    public FavoritesPage goToFavorites() {
        find(favoritesLink).click();
        return new FavoritesPage(driver, wait);
    }
}