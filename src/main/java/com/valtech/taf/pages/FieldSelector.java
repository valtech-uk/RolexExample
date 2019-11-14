package com.valtech.taf.pages;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

/*
 * Class to retrieve an object of WebElement type
 * in a class of BasePage type
 * To be used by BasePage class for getting WebElement fields from the class
 * by Valtech-UK
 * version 0.01
 * from 15.08.2019
 */
public class FieldSelector
{
    private static final Logger LOGGER = Logger.getLogger(FieldSelector.class);
    @Getter @Setter private WebElement element;
    private AbstractPage page;

    /*
     * @param name actual name of the field to be found in a class of BasePage type
     * @param page BasePage object containing the class to be found
     *
     */
    public FieldSelector(String name, AbstractPage page){
        this.page = page;
        populateElement(name);
    }

    /*
     * <h1>populateElement method</h1>
     * void method to populate the class variable element
     * @param name actual name of the field to be found
     * @throws NoSuchMethodException when unable to locate the Page Model class in the package "pages"
     * @throws IllegalAccessException when unable to get new instance of Constructor object
     *
     */
    final void populateElement(String name)
    {
        try {
            if(name == null){
                throw new IllegalArgumentException("Page to derive element from must be not null"){
                };
            }
            final Field field = page.getClass().getDeclaredField(name);
            field.setAccessible(true);
            element = (WebElement)field.get(page);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            LOGGER.info(e);
            LOGGER.error(e.getMessage());
        }
    }

}
