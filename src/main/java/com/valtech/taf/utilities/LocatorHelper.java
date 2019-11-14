package com.valtech.taf.utilities;

import org.openqa.selenium.By;

/**
 * <h1>LocatorHelper</h1>
 * Contains method getLocator which returns By
 *
 * @version 1.0
 * @author  Vikrant C
 * @author Max K
 */
public final class LocatorHelper {

    private LocatorHelper(){}

    /*
     * Method to get By
     *
     * @param locator String locator
     * @param type String locator strategy
     * @return By
     */
    public static By getLocator(String locator, String type){
        By result = null;

        if ("linkText".equals(type)){
            result = By.linkText(locator);
        }
        else{
            throw new TafException("Wrong locator strategy: "+ type + ". Select valid locator strategy");
        }
        return result;
    }
}
