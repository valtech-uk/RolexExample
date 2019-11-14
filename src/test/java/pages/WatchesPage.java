package pages;

import com.valtech.taf.pages.AbstractPage;
import com.valtech.taf.utilities.ContextManager;
import org.openqa.selenium.WebDriver;

public class WatchesPage extends AbstractPage {
    public WatchesPage(WebDriver driver){super(driver);}


    @Override
    public String getUrl(){
        return ContextManager.getContext().getEnvConfiguration().getString("resources.watches");
    }
}
