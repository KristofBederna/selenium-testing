import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OrdersPage extends BasePage {

    private By ordersListing = By.className("orders-listing");

    public OrdersPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean hasOrders() {
        return isElementPresent(ordersListing);
    }
}