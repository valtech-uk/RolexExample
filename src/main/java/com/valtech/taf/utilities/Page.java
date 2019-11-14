package com.valtech.taf.utilities;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

/*
 *
 * Class to get the page source and save it as an html file
 * Has savePageSourceAsHtml method to save the page source
 * @author Vikrant Chaudhari
 * @author Max Karpov
 * @version 0.01
 * Valtech QA
 */
public final class Page {

    private static final Logger LOGGER = Logger.getLogger(Page.class);

    private Page(){}

    /*
     * Method to save the page source as an html file
     * Saves the page source of the current page as an html file
     * Html File is saved in PageSource folder in the Project directory
     * Name of the created file is based on the page url
     * Since the file name can not contains the special characters, "//" is replaced with "" and "/" & ":" is replaced with "."
     *
     * @return complete path of the created Html file
     */
    static String savePageSourceAsHtml(){
        final String htmlFilePath;
        final String pageSource = ContextManager.getDriver().getPageSource();
        htmlFilePath = System.getProperty("user.dir") + File.separator + "PageSource" +  File.separator + ContextManager.getDriver().getCurrentUrl()
                .replace("//", "").replace("/", ".").replace(":",".") +".html";

        try {
            FileUtils.writeStringToFile(new File(htmlFilePath) ,pageSource, StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOGGER.info(e);
                LOGGER.error(e.getMessage() + " Could not save file");
            }

      return htmlFilePath;

    }
}
