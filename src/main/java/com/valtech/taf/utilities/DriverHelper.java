package com.valtech.taf.utilities;


/**
 * <h1>DriverHelper class</h1>
 * Contains helper methods to be used by BaseDriver class
 *
 * @version 1.0
 * @author  Vikrant C
 * @author Max K
 */

/*
 * @return the browser name being used.
 * @param none
 * @see this method may be called before BaseContext is loaded
 *
 */

/*
 * DriverHelper.getBrowserName() method to return the current driver name
 * @see this method has side effects: sets System properties in case it doesn't find them
 * @see logic: check if mode set in VM options. if so - return System.getProperties("mode")
 * @see logic: check if mode set in VM options. if not - check if exists in config, if so - return the config value, set System.setProperty(<config value>)
 * @see Logic: check if mode set in VM options. if not - check if exists in config, if not - use the default, do System.setProperty(<default>)
 * @return the name of browser being used
 * @param: none
 *
 */
public final class DriverHelper {

    private DriverHelper(){}

    /*
     * <h1>getBrowserName method</h1>
     * a method to return the browser type to be used
     * @param none
     * @return browser type basesed on the priority: returns VM Options -Dmode, if not available -
     * mode value in configuration.properties file, if not available
     * - returns headless
     *
     */
    public static String getBrowserName(){
        final String configValue = ConfigHelper.getPropertyFromFile("mode","configuration.properties");
        final String vmOptionsValue = System.getProperty("mode");
        String result = vmOptionsValue;

        if(vmOptionsValue == null) {

            if (configValue == null) {
                result = "headless";
                System.setProperty("mode", result);

            } else {
                result = configValue;
                System.setProperty("mode", result);
            }
        }

        return result;
    }
}

