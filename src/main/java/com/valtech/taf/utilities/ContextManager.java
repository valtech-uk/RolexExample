package com.valtech.taf.utilities;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import org.openqa.selenium.WebDriver;
/*
 * <h1>ContextManager class</h1>
 * The class to hold WebDriver and BaseContext instances
 * @author V. Chaudhari
 * @author M. Karpov
 */
public final class ContextManager {
    private static final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();
    private static final ThreadLocal<BaseContext> baseContext = new ThreadLocal<>();
    private static final ThreadLocal<BrowserMobProxyServer> proxyServer = new ThreadLocal<>();
    private static final ThreadLocal<Har> har = new ThreadLocal<>();

    private ContextManager(){}

    /*
     * <h1>getDriver method</h1>
     * @param none
     * @return WebDriver object that was set with setWebDriver method
     *
     */
    public static WebDriver getDriver() {
        return webDriver.get();
    }

    /*
     * <h1>setWebDriver method</h1>
     * @param WebDriver
     * @return none
     */
    public static void  setWebDriver(WebDriver driver) {
        webDriver.set(driver);
    }

    /*
     * <h1>getContext method</h1>
     * @param none
     * @return BaseContext object
     */
    public static BaseContext getContext(){ return baseContext.get();}

    /*
     * <h1>setContext method</h1>
     * @param BaseContext
     * @return none
     */
    public static void setContext(BaseContext context){baseContext.set(context);}


    /*
     * <h1>getProxy method</h1>
     * @param none
     * @return proxy object of BrowserMobProxyServer type
     */
    public static BrowserMobProxyServer getProxy(){
        return proxyServer.get();
    }

    /*
     * <h1>setProxy method</h1>
     * @param proxyServer
     * @return none
     */
    public static void setProxy(BrowserMobProxyServer proxy){proxyServer.set(proxy);}

    /*
     * <h1>getHar method</h1>
     * @param none
     * @return Har object
     */
    public static Har getHar(){
        return har.get();
    }
    /*
     * <h1>setHar method</h1>
     * @param Har object
     * @return none
     */
    public static void setHar(Har h){
        har.set(h);
    }



    /*
     * <h1>kill method</h1>
     * a void method to destroy an instance of the WebDriver
     * @param none
     * @return none
     */

    public static void kill(){
        webDriver.remove();
    }

}
