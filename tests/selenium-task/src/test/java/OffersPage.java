import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OffersPage extends BasePage {
    private By locationBasedOffer = By.xpath("//*[@id=\"__next\"]/main/div/div/div");

    public OffersPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public By getLocationBasedOffer() {
        return locationBasedOffer;
    }

    public String getLocationBasedOfferText() {
        return find(locationBasedOffer).getText().trim();
    }
}
