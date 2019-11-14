package com.valtech.taf.utilities;

import cucumber.api.Scenario;
import org.apache.log4j.Logger;
import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * <h1>CommonHelper class</h1>
 * Class to provide common methods to be used across the framework
 * @author Max Karpov
 * @author Vikrant Chaudhari
 */
public final class CommonHelper {

    private static final Logger LOGGER = Logger.getLogger(CommonHelper.class);
    private static final String MASK = "\\?";
    private static final int LIMIT = 35;
    private static final String ANYSLASH = "/|\\\\";

    private CommonHelper(){}
    /*
     * <h2>getTimeStamp method</h2>
     * @param none
     * @return current timestamp
     * @see depends on resources.formats.timestamp in config.xml for the timestamp format
     */
    public static String getTimeStamp(){
        String result = "";
        final String format = getDateTimeFormat();
        try {

            result = LocalDateTime.now().format(DateTimeFormatter.ofPattern(getDateTimeFormat()));

        } catch (IllegalArgumentException e) {
            LOGGER.info(e);
            LOGGER.error(e.getMessage() + CommonHelper.getError("error-get-timestamp",format));
        }
        return result;
    }

    /*
     * <h1>getDateTimeFormat method</h1>
     * Method to get the timestamp format
     * @param none
     * @see depends on resources.formats.timestamp entry in config.xml
     * @return datetime format
     * @see if problem getting the value from config.xml - @return default yyyy.MM.dd.HH.mm.ss
     */
    public static String getDateTimeFormat(){
        String format = "yyyy.MM.dd.HH.mm.ss";
        try {
            final BaseContext baseContext = new BaseContext();
            if(baseContext.getFrameworkConfiguration() == null){
                throw new IllegalArgumentException("Framework configuration is null");
            }
            format = baseContext.getFrameworkConfiguration().getString("resources.formats.timestamp");
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            LOGGER.info(e);
        }
        return format;
    }

    /*
     * <h1>parseByPattern method</h1>
     * method used to get a value from string based on regex pattern
     * @param body actual string to parse the value from
     * @param patten actual regex
     * @param group
     * @return value parsed from the string by regex
     */
    public static String parseByPattern(String body, String pattern, int group){
        String result = "";
        final Pattern pat = Pattern.compile(pattern);
        final Matcher matcher = pat.matcher(body);
        int i = 0;
        while(matcher.find()){
            if(i == group){
                result = matcher.group(i);
                break;
            }else{
                i ++;
            }

        }
        return result;
    }

    /*
     * <h1>getRegexFromTimeStamp method</h1>
     * @param timestamp - actual timestamp to be converted
     * @return regex
     */
    public static String getRegexFromTimeStamp(String timestamp){
        StringBuilder result = new StringBuilder();
            final char[] chars = timestamp.toCharArray();
            final String PART = "[0-9]";
            int i = 0;
            for(char ch:chars){
                if(Character.isAlphabetic(ch)){
                    i ++;
                }else{
                     result.append(PART + "{" + i + "}" + ch);
                     i = 0;
                }
            }
            result.append(PART + "{" + i + "}");
        return result.toString();
    }
    /*
     * <h1>getScenarioTestId methodd</h1>
     * The method used to construct a short id constructed from the first 5 chars of scenario name
     * and current time in long format and used to uniquely identify the scenario in logs
     * @param scenario - cucumber.api.Scenario type
     * @return scenarioid in String form
     *
     */
    public static String getScenarioTestId(Scenario scenario){
        final int limit = Math.min(LIMIT,scenario.getName().length());
        return scenario.getName().substring(0,limit) + "-" + Instant.now().toEpochMilli();
    }
    /*
     * <h1>getError method </h1>
     * The method to construct a standard error from the config string
     * @param errorName - the entry in the config.xml under "errors" section
     * @param location - the exact resource the framework has a problem with
     */
    public static String getError(String errorName,String location){
        final BaseContext context = ContextManager.getContext();
        String errorTemplate = context.getFrameworkConfiguration().getString("errors." + errorName);
        return replaceValues(errorTemplate,MASK,new String[]{location,context.getScenarioTestId()});
    }

    /*
     *<h1>createDirForFile method</h1>
     * a method to create path if not exist
     * @param a full path of the file to create
     * @return none
     *
     */
    public static void createDirForFile(String filePath) {
        final String [] paths = filePath.split(ANYSLASH);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<paths.length-1; i++){
            builder.append(File.separator + paths[i]);
            File dir = new File(builder.toString());
            if(!dir.exists()){
                    dir.mkdir();
            }
        }
    }

    /*
     * <h1>replaceValues method </h1>
     * a method to replace one by one all occurences of a charsequence in a string with members of string array
     * @param string - a string to be modified
     * @param find - char sequence to be replaced
     * @param replace - string array of replacements
     * @return resulting modified string
     */
    public static String replaceValues(String string,String find, String[] replace){

        List<String> temp = new ArrayList<>();
        Arrays.asList(string.split(find)).forEach(temp::add);
        if(string.endsWith(find.replace("\\",""))){
            temp.add("");
        }
        String[] parts = temp.toArray(new String[temp.size()]);
        StringBuilder result = new StringBuilder(parts[0]);
        for(int i = 1; i < parts.length; i++){
            result.append(replace[i - 1]);
            result.append(parts[i]);
        }
        return result.toString();
    }

}
