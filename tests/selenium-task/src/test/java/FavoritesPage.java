import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FavoritesPage extends BasePage {
    private By footer = By.id("custom-footer");

    public FavoritesPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public WebElement getFooterElement() {
        return find(footer);
    }
}
