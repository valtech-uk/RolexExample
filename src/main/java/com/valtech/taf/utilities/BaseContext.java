package com.valtech.taf.utilities;

import cucumber.api.Scenario;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.File;

/*
 *
 * Class to maintain the test context through the test journey
 * Uploads the contents on config.xml file on test start
 * Uploads the contents on environment specific xml file based on the environment on test start
 * @author Max Karpov
 * @author Vikrant Chaudhari
 * @version 0.01
 * Valtech QA
 */
public final class BaseContext
{
    @Getter
    private String environment;
    @Getter
    private String baseUrl;
    @Getter
    private String browser;
    @Getter
    private XMLConfiguration envConfiguration;
    @Getter
    private XMLConfiguration frameworkConfiguration;

    @Getter @Setter
    private Scenario scenario;

    @Getter @Setter
    private String scenarioTestId;

    @Getter @Setter
    private String timestamp;

    private static final String CONFIG = "configuration.properties";

    private static final Logger LOGGER = Logger.getLogger(BaseContext.class);

    public BaseContext(String env) {
        this.environment = env;
        init(environment);
    }

    public BaseContext(){
        initFrameworkConfig();
    }

    /*
     * method to populate configuration variable with the contents of config.xml file
     * method to populate configuration variable with the contents of environment specific xml file
     * @throws ConfigurationException when unable to load the config file
     * @see uses "environment.config.path" entry in configuration.properties file to get the location of config.xml file
     * config file is located at /configuration folder
     * Environment specific config files are located at /configuration folder
     * internally calls the method initFrameworkConfig which initialises the framework specific configuration file
     * Initialises config. xml which contains framework specific configuration
     * Initialises environment specific xml file based on the environment passed at run time
     * Initialises baseUrl with the respective environment url based on the environment received
     * Initialises browser with the respective browser received based on the environment
     */

    private void init(final String environment) {
        final Configurations configs = new Configurations();

        try{
            this.envConfiguration = configs.xml(System.getProperty("user.dir")
                    + ConfigHelper.getPropertyFromFile("environment.config.path",CONFIG) + File.separator +
                    "config."+ environment + ".xml");
        }catch( ConfigurationException e){
            LOGGER.error(CommonHelper.getError("error-read-config",
                    ConfigHelper.getPropertyFromFile("environment.config.path",CONFIG)
                            + File.separator + "config."
                            + environment + ".xml") + e);
            LOGGER.info(e.getMessage());
        }
        baseUrl = envConfiguration.getString("resources.base-url");
        browser = DriverHelper.getBrowserName();

        initFrameworkConfig();
    }

    /*
     * method to populate configuration variable with the contents of config.xml file
     * @throws ConfigurationException when unable to load the config file
     * config file is located at /configuration folder in project directory
     * Initialises config. xml which contains framework specific configuration
     */

    private void initFrameworkConfig(){
        final Configurations frameworkConfigs = new Configurations();

        try{

            this.frameworkConfiguration = frameworkConfigs.xml(System.getProperty("user.dir")
                    + ConfigHelper.getPropertyFromFile("framework.config.path",CONFIG));
        }catch( ConfigurationException e){
            LOGGER.error(e);
            LOGGER.info(e.getMessage());
        }
    }
}

