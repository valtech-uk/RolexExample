package com.valtech.taf.controls;

import com.valtech.taf.utilities.BaseDriver;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;



/**
 * <h1>WebControl</h1>
 * Contains WebElement wrapper functions
 * Provides ability to get the WebElement based on the name
 *
 * @version 1.0
 * @since 2019-08-20
 */
public class WebControl {

    private BaseDriver driver;
    protected WebElement element;
    private WebDriverWait webDriverWait;

    private static final long WEBDRIVER_WAIT = 5L;
    private static final long FLUENT_WAIT = 60;
    private static final long POLLING_INTERVAL = 500;

    private static final Logger LOGGER = Logger.getLogger(WebControl.class);


    public WebControl(BaseDriver driver, By locator) {
        this.driver = driver;
        this.element = this.driver.getWdriver().findElement(locator);
        webDriverWait = new WebDriverWait(this.driver.getWdriver(),WEBDRIVER_WAIT);
    }

    public WebControl(WebDriver driver, WebElement element){
        this.element = element;
        this.driver = new BaseDriver(driver);
        webDriverWait = new WebDriverWait(driver,WEBDRIVER_WAIT);
    }

    /*
     * Method to click on the web element
     * Uses ExpectedConditions class and class elementToBeClickable
     * Waits for the element to be clickable and then clicks on the element
     */
    public void click(){
        webDriverWait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /*
     * Method to click on the web element
     * Internally uses ExpectedConditions class
     * Waits for the java script to load
     * Scrolls to the element
     * Waits for the element to be visible
     * Waits for the element to be clickable
     */
    public void waitElementAndClick() {
        this.scrollElementIntoMiddleView(element);
        WebDriverWait wait = new WebDriverWait(this.driver.getWdriver(), WEBDRIVER_WAIT);
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /*
     * Method to scroll up to the element
     * Uses java script executor
     *
     * @param webElement WebElement to scroll upto
     */
    private void scrollElementIntoMiddleView(WebElement webElement) {
        String scrollElementIntoMiddleJS = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);" +
                "var elementTop = " + "arguments[0].getBoundingClientRect().top;window.scrollBy(0, elementTop-(viewPortHeight/2));";
        JavascriptExecutor je = (JavascriptExecutor)this.driver.getWdriver();
        je.executeScript(scrollElementIntoMiddleJS, webElement);
    }

    /*
     * Method to decide if the element is displayed on web page
     * User WebDriverWait
     * Waits for 10s to for the web element to be visible
     *
     * @return true if the web element is displayed, false otherwise
     */
    public boolean isDisplayed() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver.getWdriver(), WEBDRIVER_WAIT);
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (StaleElementReferenceException | TimeoutException var3) {
            LOGGER.error(var3);
            return false;
        }
        return true;
    }

    /*
     * Method to set the text in text box
     * First waits for 10s for the element to be visible
     * Clears the previous text if exists in the text box
     * uses sendKeys function to set the text
     *
     * @param text text that needs to be set
     */
    public void setTextWithTimeout(String text) {
        WebDriverWait wait = new WebDriverWait(this.driver.getWdriver(), WEBDRIVER_WAIT);
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    /*
     * Method to the text from Web Element
     *
     * @param element WebElement whose text needs to be extracted
     * @return String Returns the text of the Web Element
     */
    public String getText() {
        String text;
        WebDriverWait wait = new WebDriverWait(this.driver.getWdriver(), WEBDRIVER_WAIT);
        wait.until(ExpectedConditions.visibilityOf(element));
        text = element.getAttribute("value");

        return text;
    }

    /*
     * Method to wait until the dropdown is loaded
     * Wait until the first element in the dropdown is loaded
     */
    private void waitDropdown() {
        final Select dropList = new Select(element);
        ExpectedCondition<Boolean> dropdownLoad = (WebDriver webDriver)-> dropList.getOptions().size() > 1;

        (new FluentWait(this.driver.getWdriver())).withTimeout(Duration.ofSeconds(FLUENT_WAIT)).pollingEvery(Duration.ofMillis(POLLING_INTERVAL))
                .until(dropdownLoad);
    }

    /*
     * Method to select from dropdown
     * Calls waitDropdown method to wait for the dropdown to load
     * @param visibleText Text which needs to be selected from dropdown
     */
    public void selectFromDropdown(String visibleText) {
        waitDropdown();
        (new Select(element)).selectByVisibleText(visibleText);
    }

    /*
     * Method to get the selected option in the dropdown

     * @return text of the selected item in the dropdown
     */
    public String getSelectedItemText() {
        Select select = new Select(element);
        WebElement option = select.getFirstSelectedOption();
        return option.getText();
    }

    /*
     * Method to check the checkbox or a radio button
     * Check if the element is not selected, otherwise selects it
     */
    public void check() {
        if (!element.isSelected()) {
            waitElementAndClick();
        }
    }

    /*
     * Method to get the selected option in the dropdown

     * @return true if the element is selected, otherwise false
     */
    public boolean isSelected() {
        return element.isSelected();
    }
  }


