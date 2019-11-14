package com.valtech.taf.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.Setter;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.apache.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * <h1>BaseDriver</h1>
 * Provides ability to switch between drivers based on VM options
 * or properties file configuration including remote driver<br>
 * Supported drivers: chrome, firefox, ie, edge, safari, remote, android, ios
 *
 * @version 1.0
 * @author  Vikrant C
 * @author Max K
 */

public final class BaseDriver {

    @Getter @Setter
    private WebDriver wdriver;
    private static WebDriver webDriver;
    private static final String CHROME = "chrome";

    private static final Logger LOGGER = Logger.getLogger(BaseDriver.class);

    public BaseDriver(WebDriver driver)  {
        this.wdriver = driver;
    }

    /*
     * Method to get driver instance
     * This method checks the mode.
     * If mode is null, it takes the browser name from configuration.properties file
     * If the mode is not null, it takes the browser given in mode
     * Supported drivers: chrome, firefox, edge, ie, safari, remote, headless, proxy
     * Throw runtime TafBrowserException when an invalid browser name is specified.
     *
     * @throws MalformedURLException
     * @see depends on getHeadlessDriver() to get headless instance
     */
    public static WebDriver getDriver() {
        String driverName;

        try{
            if (null != System.getProperty("mode")){
                driverName = System.getProperty("mode");
            }else {
                driverName= ConfigHelper.getPropertyFromFile("mode", "configuration.properties");
            }

            if (driverName == null) {
                driverName = CHROME;
            }
            switchDriver(driverName);

        }catch (MalformedURLException e){
            LOGGER.error(e);
        }
        ContextManager.setWebDriver(webDriver);
        return webDriver;
    }

    /*
     * Method to initialise the driver based on the browser name passed
     * This private method is used as a helper method to initialise the driver
     * Supported drivers: chrome, firefox, edge, ie, safari, remote, headless, proxy
     * Throw runtime TafBrowserException when an invalid browser name is specified.
     * @return none
     * @throws MalformedURLException
     * @param driverName Driver name
     */
    private static void switchDriver(String driverName) throws MalformedURLException {
        switch (driverName) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                webDriver = new ChromeDriver();
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
                break;

            case "safari":
                System.setProperty("webdriver.safari.driver", new BaseContext(EnvHelper.getEnvironment()).getFrameworkConfiguration()
                        .getString("resources.drivers.safari"));
                webDriver = new SafariDriver();
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                webDriver = new EdgeDriver();
                break;

            case "ie":
                WebDriverManager.iedriver().setup();
                InternetExplorerOptions ieOptions = new InternetExplorerOptions();
                ieOptions.ignoreZoomSettings();
                webDriver = new InternetExplorerDriver(ieOptions);
                break;

            case "remote":
                webDriver = getRemoteDriver();
                break;

            case "headless":
                webDriver = getHeadlessDriver();
                break;

            case "proxy":
                webDriver = getProxyDriver();
                break;

            default:
                throw new TafBrowserException("Possible values are: chrome, firefox, edge, ie, safari, remote, headless, proxy");
        }
    }

    /*
     * Method to get driver instance
     * This overloaded version can be called by passing the browser name as string argument
     * internally calls the method switchDriver
     * Supported drivers: chrome, firefox, ie, edge, safari, remote, android, ios
     * If driver is null, it initialises it by Chrome
     * Throw runtime TafBrowserException when an invalid browser name is specified.
     *
     * @throws MalformedURLException
     * @param driverName Driver name
     * @return WebDriver
     * @see using switchDriver to switch between drivers
     * @see defaults to chrome
     */
    public static WebDriver getDriver(String driverName) throws MalformedURLException {
        if (driverName == null) {
            driverName = CHROME;
        }

        switchDriver(driverName);
        return webDriver;
    }

    /*
     * <h1>getHeadlessDriver method</h1>
     * a private method used by SwitchDriver() method to initialize a headless driver
     * @param none
     * @return WebDriver object
     * @see the type of driver is Chrome
     */
    private static WebDriver getHeadlessDriver(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        return new ChromeDriver(options);
    }


    /*
     * <h1>getProxyDriver method</h1>
     * a public method used by SwitchDriver() method to initialize a proxy driver
     * @param none
     * @return WebDriver object
     * @see method is used to capture http headers by AssertHelper.getHeaders() method
     */
    public static WebDriver getProxyDriver(){
        BrowserMobProxyServer proxy = new BrowserMobProxyServer();
        proxy.setHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS);
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS,CaptureType.RESPONSE_HEADERS);
        System.setProperty("bmp.allowNativeDnsFallback", "true");
        System.setProperty("java.net.preferIPv4Stack","true");

        proxy.start();

        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        seleniumProxy.setSslProxy("trustAllSSLCertificates");

        seleniumProxy.setHttpProxy("localhost:" + proxy.getPort());
        seleniumProxy.setSslProxy("localhost:" + proxy.getPort());

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors");

        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        options.setCapability(CapabilityType.PROXY, seleniumProxy);
        options.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);

        webDriver = new ChromeDriver(options);
        proxy.newHar(ContextManager.getContext().getScenarioTestId());
        Har har = proxy.getHar();
        ContextManager.setProxy(proxy);

        ContextManager.setHar(har);
        return webDriver;
    }

    /*
     * <h1>getRemoteDriver method</h1>
     * a public method used by SwitchDriver() method to initialize a remote driver
     * @param none
     * @return WebDriver object
     * @see accesses default hub url http://localhost:4444/wd/hub
     */
    public static WebDriver getRemoteDriver() throws MalformedURLException{
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-debugging-port=9222");
        options.addArguments("--headless");
        options.setCapability("platform",Platform.LINUX);
        webDriver = new RemoteWebDriver((new URL("http://localhost:4444/wd/hub")), options);
        return webDriver;
    }

 }

