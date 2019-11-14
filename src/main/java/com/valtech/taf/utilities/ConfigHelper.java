package com.valtech.taf.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/*
 * Class to contain some common static methods to be used by BaseContext class
 * @author Max Karpov
 * @author Vikrant Chaudhari
 * Valtech QA
 * @version 0.01
 */
public final class ConfigHelper {

    private static final Logger LOGGER = Logger.getLogger(ConfigHelper.class);
    private ConfigHelper(){}

     /*
      * @param key name of the property to be set
      * @param value actual value of the property to be set
      * @param file the name of the file to be updated
      * @throws IOException when unable to access input stream
      * @throws IOException when unable to access output stream
      * Currently writes timestamp at the top of the file
      */

    public static void  setPropertyInFile(String key, String value, String file){
        Properties prop = new Properties();

        try( FileInputStream fileInputStream = new FileInputStream(file) ) {
            prop.load(fileInputStream);
        } catch (IOException e){
            LOGGER.error(e);
        }

        try( FileOutputStream fileOutputStream = new FileOutputStream(file) ) {
            if(value != null){
                prop.setProperty(key,value);
            }
            else{
                prop.remove(key);
            }

            prop.store(fileOutputStream,null);
         } catch (IOException e){
            LOGGER.error(e);
         }
    }

    /*
     * Method to get the property from file based on the key specified
     * @param key name of the property to be get
     * @param file the name of the file to be updated
     * @throws IOException when unable to access input/output streams
     */
    public static String getPropertyFromFile(String key, String file){
        String result = null;
        Properties prop = new Properties();
        try(
                FileInputStream fileInputStream = new FileInputStream(file)
        ){
            prop.load(fileInputStream);
            result = prop.getProperty(key);
        } catch (IOException e){
            LOGGER.error(e);
        }
        return result;
    }

    /*
     * @param key name of the property to be set
     * @param file the name of the file to be updated
     * @param defaultValue Gets the default value when passed key is not found in the file
     * @throws IOException when unable to access input/output streams
     */
    public static String getPropertyFromFile(String key, String file, String defaultValue){
        String result = null;
        Properties prop = new Properties();
        try(
                FileInputStream fileInputStream = new FileInputStream(file)
        ){
            prop.load(fileInputStream);
            result = prop.getProperty(key,defaultValue);
        } catch (IOException e){
            LOGGER.error(e);
        }
        return result;
    }

    /*
     * <h1>getProperties method</h1>
     * The method to get Properties object from a file
     * @param path to the file
     * @return properties
     * @throws IOException
     */
    public static Properties getProperties(String path){
        Properties properties = new Properties();
        try(
                FileInputStream fileInputStream = new FileInputStream(new File(path))
        ){

                properties.load(fileInputStream);

        } catch (IOException e){
            LOGGER.error(CommonHelper.getError("get-properties-error",path) + " " + e.getMessage());
            LOGGER.info(e);

        }
        return properties;
    }

}
