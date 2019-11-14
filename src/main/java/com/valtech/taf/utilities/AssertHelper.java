package com.valtech.taf.utilities;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;


/*
 * <h1>AssertHelper class</h1>
 * Helper class to provide methods used by custom Assert classes
 * Methods are invoked when a test fails
 * @author Max Karpov
 * @author Vikrant Chaudhari
 * used by TafAssert class
 */
public final class AssertHelper {

    private static final String MASK = "\\?";
    private static final int TIMEOUT = 3;
    private static final String USER_DIR = "user.dir";
    private static final Logger LOGGER = Logger.getLogger(AssertHelper.class);


    private AssertHelper(){}
    /*
     * <h2>saveHtml method</h2>
     * Method to save HTML code of the current page to an html file
     * @param driver WebDriver object currently used in test
     * @return none
     * @see depends on resources.paths.assert-html-log entry in config.xml file to store logs to
     * @see default log directory if nothing in config.xml - /log/html-logs
     * @see depends on VM options -Dmode
     * @see depends on VM options -Denvironment
     * @throws RuntimeException
     * @throws IOException
     * @throws IllegalArgumentException
     */



    public static void saveHtml(){
        final WebDriver driver = ContextManager.getDriver();
        BaseContext context = new BaseContext();

        if(driver!=null) {
            final String htmlSource = driver.getPageSource();
            final String browser = System.getProperty("mode");
            final String environment = System.getProperty("environment");
            String scenario = System.getProperty("scenario");
            String timestamp = CommonHelper.getTimeStamp();
            String srcName = scenario + "-Source-HTML-" + environment + "-" + browser + "-"
                    + timestamp + ".html";
            String userDir = System.getProperty(USER_DIR) + File.separator;


            String path = userDir + File.separator + "log" + File.separator + "html-log" + File.separator;
            try {
                path = userDir + context.getFrameworkConfiguration().getString("resources.paths.assert-html-log");
            } catch (RuntimeException e) {
                LOGGER.error("Error at scenario " + scenario + e.getMessage());
                LOGGER.info(e);
            }
            try {

                FileUtils.writeStringToFile(new File(path + srcName), htmlSource, getEncoding());
            } catch (IOException | IllegalArgumentException e) {
                LOGGER.info(e);
                LOGGER.error(e.getMessage() + " Could not save file for scenario " + scenario);
            }
        }
    }

    /*
     * <h1>getScreenshot method</h1>
     * A method to create screenshot for the failing scenario
     * @param driver a WebDriver object active at the moment of the failure
     * @see this method will create screenshot if VM options "screenshot=true" is available
     * @see depends on resources.paths.reports.screenshot entry in config.xml
     * @throws IOException
     * @throws AssertionError
     */
    public static String getScreenshot(String scenario, String timestamp){
        BaseContext context = ContextManager.getContext();
        File screenshot;
        String screenshotPath = "";
        LOGGER.debug(context.getScenarioTestId() + ": Taking screenshot with timestamp: " + timestamp);
        if(takeScreenshot()) {
            String path = CommonHelper.replaceValues(System.getProperty(USER_DIR)
                    + File.separator
                    + context.getFrameworkConfiguration()
                    .getString("resources.paths.reports.screenshot"),MASK,new String[]{scenario,timestamp});

            CommonHelper.createDirForFile(path);
            WebDriver driver =  ContextManager.getDriver();
            if(driver != null) {
                screenshotPath = screenshotPath + path;
                screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

                Path src = screenshot.toPath();
                try {
                    Files.move(src,src.resolveSibling(path));
                } catch (IOException e) {
                    LOGGER.error(CommonHelper.getError("error-save-screenshot",path));
                    LOGGER.info(e);
                }
            }
        }
        return screenshotPath;
    }

    /*
     * <h1>takeScreenshot method</h1>
     * Method that returns true or false if driver being used is headless or remote
     * and -Dscreenshot is not set or is not false
     * If -Dscreenshot=true will return true with any driver
     * Can return true if not headless if -Dscreenshot=true
     * @param none
     * @return boolean value (to take or not to take screenshot)
     */
    private static boolean takeScreenshot() {
        boolean result = false;
        final String mode = System.getProperty("mode");
        if(mode!=null){
        final String screen = System.getProperty("screenshot");
        if ("headless".equals(mode)
        || "remote".equals(mode)
        ) {
            if (screen != null && ! "false".equals(screen)) {
                result = true;
            }
            if(screen == null){
                result = true;
            }
        } else if (screen != null && "true".equals(screen)) {
            result = true;
        }else{
            result = false;
        }
        }
        return result;
        }
    /*
     * <h1>getEncoding method</h1>
     * private method used to get the encoding type from config file
     * @param none
     * @return encoding type
     * @see depends on the entry resources.encoding.html-log in config.xml file
     * @see defaults to "UTF-8"
     */
    private static String getEncoding(){
        String encoding = "UTF-8";

            final BaseContext context = new BaseContext();
            if(context.getFrameworkConfiguration() != null) {
                encoding = context.getFrameworkConfiguration().getString("resources.encoding.html-log");
            }
        return encoding;

    }

    /*
     * <h1>saveHeaders method</h1>
     * public method used to save HAR files when the test fails
     * @param none
     * @return none
     * @throws IOException
     * @see depends on resources.paths.reports.header in config.xml file
     * @see amends log4j-application.log when unable to save file
     */
    public static void saveHeaders(){

        BaseContext context = ContextManager.getContext();

        File harFile = new File(System.getProperty(USER_DIR) +
                File.separator + context.getFrameworkConfiguration()
                .getString("resources.paths.reports.header")
                .replace("?",context.getScenarioTestId()));
        WebDriver driver = ContextManager.getDriver();
        BrowserMobProxyServer proxy = ContextManager.getProxy();

//        proxy.newHar(context.getScenarioTestId());
        Har har = ContextManager.getHar();

//        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);
//        driver.navigate().refresh();
//        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);

        try {
            CommonHelper.createDirForFile(harFile.getAbsolutePath());
            har.writeTo(harFile);
        } catch (IOException e) {
            LOGGER.info(e);
            LOGGER.error(e.getMessage() + CommonHelper.getError("error-save-har",harFile.getName()));
        }


    }

}

