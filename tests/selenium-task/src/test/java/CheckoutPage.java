import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage extends BasePage {

    private By firstName = By.id("firstNameInput");
    private By lastName = By.id("lastNameInput");
    private By address = By.id("addressLine1Input");
    private By province = By.id("provinceInput");
    private By postal = By.id("postCodeInput");
    private By submit = By.id("checkout-shipping-continue");

    public CheckoutPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public ConfirmationPage fillForm(String f, String l, String a, String p, String pc) {
        find(firstName).sendKeys(f);
        find(lastName).sendKeys(l);
        find(address).sendKeys(a);
        find(province).sendKeys(p);
        find(postal).sendKeys(pc);
        find(submit).click();
        return new ConfirmationPage(driver, wait);
    }
}