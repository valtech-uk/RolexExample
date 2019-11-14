package com.valtech.taf.utilities;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <h1>EnvHelper</h1>
 * Provides ability to get the Url of the environment passed
 *
 * @version 1.0
 * @author  Vikrant C
 * @author Max K
 */
public final class EnvHelper {

    private static final String ENVIRONMENT = "environment";
    private static final String CONFIG = "configuration.properties";
    private static final Logger LOGGER = Logger.getLogger(EnvHelper.class);

    private EnvHelper(){}

    /*
     * Method to get the environment
     * Returns the environment set in VM Options
     * If VM Options does not have environment then returns the environment mentioned in configuration.properties file
     * If environment is neither set in VM Options nor in configuration.properties, default dev environment is returned
     * Sets the selected environment in VM Options
     *
     * @throw TafException
     * @return respective environment.
     */
    public static String getEnvironment(){
        final String defaultEnv;
        BaseContext baseContext = new BaseContext();
        pallySetUp();
        defaultEnv = baseContext.getFrameworkConfiguration().getString("resources.environment.default");
        final String vmOptionsEnvValue = System.getProperty(ENVIRONMENT);

        if (defaultEnv == null && vmOptionsEnvValue == null &&
                ConfigHelper.getPropertyFromFile(ENVIRONMENT,CONFIG ) == null){
            throw new TafException("Fall back default environment is not set in framework configuration. Please set it with appropriate value or set"
                    + " the environment in VM options or in configuration.properties file");
        }

        String environment;

        if (vmOptionsEnvValue != null){
            environment = vmOptionsEnvValue;
        }else {
            environment = ConfigHelper.getPropertyFromFile(ENVIRONMENT, CONFIG,defaultEnv);
        }

        System.setProperty(ENVIRONMENT, environment);
        return environment;
    }

    /*
     * Method to do environment setup for Pa11y tests execution
     * Creates the folder PallyTextResults in project directory
     * Creates the folder PageSource in project directory
     * Creates the folder PallyResults in project directory
     * This method is invoked when getEnvironment method is called.
     */
    public static void pallySetUp() {
        String userDirectory = System.getProperty("user.dir");
        Path pallyResultsDirectory = Paths.get(userDirectory + File.separator + "PallyResults");
        Path pageSourceDirectory = Paths.get(userDirectory + File.separator + "PageSource");
        Path pallyTextResultsDirectory = Paths.get(userDirectory + File.separator + "PallyTextResults");

        createDirectory(pallyResultsDirectory);
        createDirectory(pageSourceDirectory);
        createDirectory(pallyTextResultsDirectory);
    }

    /*
     * Private method to create the directory
     * First checks if the directory already exists, if not then creates the directory
     *
     * @param path PAth of the directory that needs to be created.
     */
    private static void createDirectory(Path path){
        if (!path.toFile().exists()){
            try{
                Files.createDirectory(path);
            }catch (IOException e){
                LOGGER.error(e);
            }
        }
    }
}
