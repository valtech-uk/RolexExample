package pages;

import com.valtech.taf.pages.AbstractPage;
import com.valtech.taf.utilities.ContextManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class HomePage extends AbstractPage {

    @FindBy(how = How.CSS, using = "a[title='Menu']")
    private WebElement link_menu;

    @FindBy(how = How.CSS, using = "a[href='/watches.html']")
    private WebElement link_watches;

    @FindBy(how = How.CSS, using = "h2[class='rlxr-text-title']")
    private WebElement h2_title;



    public HomePage(WebDriver driver){super(driver);}

    @Override
    public String getUrl(){
        return ContextManager.getContext().getBaseUrl();
    }
}
