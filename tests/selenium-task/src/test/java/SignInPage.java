import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SignInPage extends BasePage {

    private By loginButton = By.id("login-btn");

    public SignInPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public HomePage login(String username, String password) {
        selectDropdownOptionUsername(username);
        selectDropdownOptionPassword(password);
        find(loginButton).click();
        return new HomePage(driver, wait);
    }
}