import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ConfirmationPage extends BasePage {

    private By continueShopping = By.className("optimizedCheckout-buttonSecondary");

    public ConfirmationPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public HomePage continueShopping() {
        find(continueShopping).click();
        return new HomePage(driver, wait);
    }
}