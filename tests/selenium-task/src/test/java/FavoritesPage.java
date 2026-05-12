import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FavoritesPage extends BasePage {
    By favoriteItems = By.className("shelf-container");

    public FavoritesPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean hasFavorites() {
        return find(favoriteItems).isDisplayed();
    }
}
