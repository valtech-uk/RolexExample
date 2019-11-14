/*
 * <h1>qa-framework</h1> - common Java framework to be used by Valteh QA
 * BasePage - Base POM class to be inherited from by all the POM classes
 * Initializes the WebElement variables with WebDriver
 * Provides the ability to get an instance of a child class by its name
 * Provides the ability to get a WebElement by its name
 * @author Max Karpov
 * @author Vikrant Chaudhari
 * @version 0.01
 * UK
 *
 *
 */

package com.valtech.taf.pages;

import com.valtech.taf.controls.WebControl;
import com.valtech.taf.utilities.CommonHelper;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public abstract class AbstractPage {

    private static final Logger LOGGER = Logger.getLogger(AbstractPage.class);
    /*
     * Dummy Element to copy from
     * How.CSS is an an example of usage
     */
    @FindBy(how = How.CSS, using = "#id")
    private WebElement baseElement;
    private WebDriver driver;

    public AbstractPage(WebDriver driver) {
        this.driver = driver;
        init(driver);
    }

    /*
     * Method to get an instance of a child class by its name
     * Looking for all classes in this package
     * To be used with a strict naming convention
     * @param name Java class name (without .java extention) to be found
     * @param driver WebDriver object to initialize the Page Model class with
     * @throws ClassNotFoundException when unable to locate the Page Model class in the package "pages"
     */
    public static AbstractPage getPage(String name, WebDriver driver) {

        AbstractPage model = null;
        try {
            Class p = Class.forName("pages." + name);
            model = getPageFromClass(p,driver);

        } catch (ClassNotFoundException e) {
            LOGGER.error(CommonHelper.getError("error-get-page",name) + e.getMessage());
            LOGGER.info(e);
        }


        return model;
    }

    /*
     * private method to be used by getPage method
     * @param cl Class object that is returned by detecting classes by Java reflection
     * @param driver WebDriver object to initialize the Page Model class with
     * @throws InstantiationException when unable to get new instance of Constructor object
     * @throws NoSuchMethodException when unable to locate the Page Model class in the package "pages"
     * @throws IllegalAccessException when unable to get new instance of Constructor object
     */
    private static AbstractPage getPageFromClass(Class cl, WebDriver driver){
        AbstractPage model = null;

        try {
            if(driver == null) {
                throw new IllegalArgumentException("Driver must be not null. " +
                        CommonHelper.getError("error-null-driver","class")) {
                };
            }
            Constructor co = cl.getConstructor(WebDriver.class);
            model = (AbstractPage) co.newInstance(driver);
        } catch (InstantiationException | NoSuchMethodException | IllegalArgumentException|
                InvocationTargetException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
            LOGGER.info(e);
        }

        return model;
    }

    /*
     * Method to get WebElement within the class by its name
     * Looking for all elements in the class
     * @param name actual name of the WebElement to be found
     */
    public WebElement getElement(String name) {
        WebElement element = null;
        FieldSelector fieldSelector = new FieldSelector(name, this);

        element = fieldSelector.getElement();
        if (element == null) {
            LOGGER.info(CommonHelper.getError("error-get-element",name));
        }

        return element;
    }

    /*
     * Method to get a WebControl initialized with WebElement called by the name parameter
     * This method uses getElement method within this class
     * @param name the actual name of WebElement stored in the current Page Object class
     * @return WebControl object
     */
    public WebControl getWebControl(String webElementName){
        WebElement element = getElement(webElementName);
        return new WebControl(driver,element);
    }


    /*
     * Method to initialize the Page Model with
     * the WebDriver being used
     * @param driver WebDriver object to initialize the Page Model class with
     */
    private void init(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    /*
     * Method to be overriden by implementation classes
     */

    public abstract String getUrl();

}
