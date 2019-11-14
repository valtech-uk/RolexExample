package pages;

import com.valtech.taf.pages.AbstractPage;
import com.valtech.taf.utilities.ContextManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;


public class ModelPage extends AbstractPage {

    @FindBy(how = How.CSS, using = "a[href='/fr/wishlist.html']")
    private WebElement link_wishlist;

    public ModelPage(WebDriver driver){super(driver);}

    @Override
    public String getUrl(){
        return ContextManager.getContext().getEnvConfiguration().getString("resources.model");
    }
}
