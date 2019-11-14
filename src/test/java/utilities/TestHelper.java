package utilities;


import com.valtech.taf.utilities.Docker;
import com.valtech.taf.utilities.ContextManager;
import com.valtech.taf.utilities.BaseDriver;
import com.valtech.taf.utilities.AssertHelper;
import com.valtech.taf.utilities.CommonHelper;
import com.vimalselvam.cucumber.listener.Reporter;
import cucumber.api.Scenario;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


/*
 * <h1>TestHelper class</h1>
 * The class to be used mainly for the framework self-test definition classes
 * @author M. Karpov
 * @author V Chaudhari
 */
public class TestHelper {
    private static final Logger LOGGER = Logger.getLogger(BaseDriver.class);


    /*
     * <h1>getProperties method</h1>
     * this method checks if the previous runs of self-test completed successfully and if otherwise it would populate configuration.properties file with defult values
     * @param none
     * @return Properties object to be then used in test definition class Before and After methods to make sure the configuration.properties is not corrupted
     * @see this method is needed to cover self-test needs only (as only in self-test configuration.properties is modified by the framework
     * @see populates configuration.properties file if corrupted
     */
    public static Properties getProperties(){
        final Properties properties = new Properties();



        if(com.valtech.taf.utilities.ConfigHelper.getPropertyFromFile("complete","configuration.properties") != null){
            try(
                    FileInputStream fileInputStream = new FileInputStream(new File("configuration.properties"))
            ){
                properties.load(fileInputStream);

            } catch (IOException e){
                LOGGER.error(e);
            }
        }else{
            properties.setProperty("mode", "chrome");
            properties.setProperty("config", "/configuration/config.xml");
            properties.setProperty("environment.config.path", "/configuration/");
            properties.setProperty("framework.config.path", "/configuration/config.xml");


            try( FileOutputStream fileOutputStream = new FileOutputStream(new File("configuration.properties")) ) {
                properties.store(fileOutputStream,null);
            } catch (IOException e){
                LOGGER.error(e);
            }

        }

        return properties;
    }

    /*
     * <h1>isValidTimestamp method</h1>
     * the method being used by def classes to check that the timestamp is what's expected
     * @param path
     * @return boolean value of whether or not the timestamp is valid
     */
    public static Boolean isValidTimestamp(String path) {
        boolean result = false;
        String regex = CommonHelper.getRegexFromTimeStamp(ContextManager.getContext().getFrameworkConfiguration().getString("resources.formats.timestamp"));
        String timestamp = CommonHelper.parseByPattern(path,regex,0);

        if(timestamp!=null && !timestamp.isEmpty()){
            result = true;
        }
        return result;
    }

    /*
     * <h1>purgeSelenium method</h1>
     * a method to stop - restart selenium containers
     * @param none
     * @return none
     */
    public static void purgeSeleniumHub(){
        Docker docker = new Docker();

        docker.stopContainer("selenium/hub:latest");
        docker.stopContainer("selenium/node-chrome-debug:latest");

        docker.startContainer("hub");
        docker.startContainer("chrome");

    }

    /*
     * <h1>log4ContainsErrorMessage</h1>
     * a method to verify if log4 file was added the expected message
     * @param error  - the error name in config.xml file
     * @param location - the file or resource that the program was unable to access/process
     * @return none
     */
    public static boolean log4ContainsErrorMessage(String error, String location){
        boolean result = false;
        String log = "log4j-application.log";
        String errorText = CommonHelper.getError(error,location);

        try {
            String logText = FileUtils.readFileToString(new File(log),"UTF-8");
            result = logText.contains(errorText);
        } catch (IOException e) {
            throw new AssertionError(log + " file is not accessible.");
        }

        return result;
    }
    /*
     * <h1>belongsToScope method</h1>
     * a placeholder method to detect whether or not to trigger @Before method in a def class
     * @param scenario
     * @param scope - the scope of the def class
     * @return result - boolean value depending on whether or not the def class should be used
     */
    public static boolean belongsToScope(Scenario scenario,String scope){
        boolean result = false;
        if(scenario.getName().contains("22")){
            if(!scope.equals("functional")){
                result = true;
            }
        }else{
            if(scope.equals("functional")){
                result = true;
            }
        }
        return result;
    }


    /*
     * <h1>completeFailure method</h1>
     * a method to be invoked in @After section of a Def class to make sure screenshots,html logs and http headers are saved
     * @param sc - the current Cucumber Scenario object
     * @see this method saves file if the current scenario failed
     */
    public static void completeFailure(Scenario sc){
        if (sc.isFailed()) {

            try {
                if(Reporter.getExtentReport() != null) {
                    if(ContextManager.getDriver()!= null) {
                        Reporter.addScreenCaptureFromPath(AssertHelper.getScreenshot(System.getProperty("scenario"), CommonHelper.getTimeStamp()));
                        AssertHelper.saveHtml();
                        AssertHelper.saveHeaders();
                    }
                }
            } catch (IOException e) {
                LOGGER.info(e);
                LOGGER.error(e.getMessage());
            }

        }
    }


    /*
     * <h1>setProperties method</h1>
     * the method to save a Properties object into a file
     * @param properties
     * @param path
     * @throws IOException
     */
    public static void setProperties(Properties properties, String path){
        try(FileOutputStream fileOutputStream = new FileOutputStream(path)){
            properties.store(fileOutputStream,null);
        }catch (IOException e){
            LOGGER.error(CommonHelper.getError("set-properties-error",path) + " " + e.getMessage());
            LOGGER.info(e);
        }
    }

    public static JSONObject parseFromFile(String src, String name){

        String res = null;
        JSONObject result = null;

        if(src!=null &! src.isEmpty()){
            String temp[] = src.split(name);
            if(temp.length > 1){
                res = temp[1].split(";")[0];
                result = new JSONObject(res);
            }
        }
        return result;
    }
}
