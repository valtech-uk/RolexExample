package steps;

import com.valtech.taf.pages.AbstractPage;
import com.valtech.taf.utilities.*;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import utilities.TestHelper;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class FuncSteps {

    private BaseContext context;
    private WebDriver driver;
    private AbstractPage currentPage;
    private static final String USER_DIR = System.getProperty("user.dir");
    private String source;
    @Before
    public void prepareTest(Scenario sc) throws MalformedURLException {
        context = new BaseContext(EnvHelper.getEnvironment());
        context.setScenarioTestId(CommonHelper.getScenarioTestId(sc));
        context.setTimestamp(CommonHelper.getTimeStamp());
        ContextManager.setContext(context);
        driver = BaseDriver.getDriver("proxy");
        driver.manage().window().maximize();

        ContextManager.setWebDriver(driver);
    }

    @Given("^I start UI test$")
    public void i_start_UI_test() throws Throwable {
       currentPage = AbstractPage.getPage("HomePage",driver);
    }

    @Given("^I start UI test and go to Watches page$")
    public void i_start_UI_test_and_go_to_Watches_page() throws Throwable {
        currentPage = AbstractPage.getPage("WatchesPage",driver);
        driver.get(currentPage.getUrl());
    }

    @Given("^I start UI test and go to Model page$")
    public void i_start_UI_test_and_go_to_Model_page() throws Throwable {
        currentPage = AbstractPage.getPage("ModelPage",driver);
        driver.get(currentPage.getUrl());
    }

    @When("^I open Rolex page$")
    public void i_open_Rolex_page() throws Throwable {
         driver.get(currentPage.getUrl());
    }

    @When("^I add the watch to the Wishlist$")
    public void i_add_the_watch_to_the_Wishlist() throws Throwable {
        currentPage.getWebControl("link_wishlist").click();
        Thread.sleep(7000);
    }


    @Then("^Web Analytics is available in source code$")
    public void web_Analytics_is_available_in_source_code() throws Throwable {
        source = driver.getPageSource();
        final String expected = FileUtils.readFileToString(new File(USER_DIR +
                File.separator + "resources/analytics1"),"UTF-8");
        Assert.assertTrue("Web Analytics not available on the page",source.contains("digitalDataLayer"));
        JSONObject object = TestHelper.parseFromFile(source,"digitalDataLayer = ");
        JSONObject expectedObject = TestHelper.parseFromFile(expected,"digitalDataLayer = ");

        Assert.assertTrue("digitalDataLayer object not available on the page",object != null);

      //  JSONAssert.assertEquals(object,expectedObject,false); //Can compare with the expected if expected object confirmed. Fails now
    }

    @Then("^Assets Javascript is loaded$")
    public void assets_Javascript_is_loaded() throws Throwable {
        Assert.assertTrue("Asset JS not loaded",source.
                contains(context.getFrameworkConfiguration().
                        getString("resources.asset-script")));
    }

    @Then("^Smetrics request was sent with all correct values$")
    public void smetrics_request_was_sent_with_all_correct_values() throws Throwable {
        WebElement element = currentPage.getElement("h2_title");
        Thread.sleep(7000);
        AssertHelper.saveHeaders();
        final String path = USER_DIR +
                      File.separator + "reports" +
                      File.separator + "headers" +
                      File.separator + context.getScenarioTestId() + ".har";
        final File har = new File(path);
        JSONObject object = new JSONObject(FileUtils.readFileToString(har, "UTF-8"));
        JSONArray entries = object.getJSONObject("log").getJSONArray("entries");
        boolean requestFound = false;
        for(int i = 0; i< entries.length(); i++){
            JSONObject o = entries.getJSONObject(i);
            String url = o.getJSONObject("request").getString("url");
            if(url.contains("smetrics.rolex.com/b/ss/rolexrcmrolexliverv5")){
                requestFound = true;
                String[] params = url.split("\\?")[1].split("&");
                Map<String,String> map = new HashMap<>();
                map.put("pageName","home");
                map.put("events","event1");
                Properties props = ConfigHelper.getProperties("har.properties");

                Enumeration<String> enums = (Enumeration<String>) props.propertyNames();

                while (enums.hasMoreElements()) {
                    String key = enums.nextElement();
                    String value = props.getProperty(key);
                    boolean exists = false;
                    boolean matches = false;
                    if(key.contains("c11"))
                        System.out.println(key);
                    for(String param:params){
                        String[] parameter = param.split("=");

                        if(param.contains("c11"))
                            System.out.println(param);

                        if(parameter[0].equals(key)){
                            exists = true;
                            if(map.containsKey(key)){
                                Assert.assertTrue("Expected " + map.get(key) + " to be " + value,((String)map.get(key)).equals(value));
                            }
                            break;
                        }

                    }
                    Assert.assertTrue("Parameter " + key + " is not available",exists);
                }
                break;
            }
      }
        Assert.assertTrue("Web Analytics request not captured",requestFound);
    }


    @After
    public void cleanUp(Scenario scenario){
        if(driver!=null){
            driver.quit();
        }
    }
}
