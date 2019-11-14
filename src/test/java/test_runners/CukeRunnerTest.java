package test_runners;


import com.valtech.taf.utilities.RunnerHelper;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import utilities.TestHelper;



@RunWith(Cucumber.class)
@CucumberOptions(
        features= {"src/test/features/test/"},
        plugin = {"pretty", "html:target/cucumber",
        "json:target/jsonReports/cucumber.json", "com.vimalselvam.cucumber.listener.ExtentCucumberFormatter:"},
        glue={"steps"}
        , tags = {}
        )
public class CukeRunnerTest {

    @BeforeClass
    public static void setup(){
        TestHelper.getProperties();
        RunnerHelper.setCucumberReportName("Functional");

    }

    @AfterClass
    public static void tearDown(){
        try {
            TestHelper.purgeSeleniumHub();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}