package com.valtech.taf.utilities;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 *
 * Class having utility methods required for Accessibility testing.
 * Uses Pa11y to run the accessibility tests on the web pages.
 * @author Vikrant Chaudhari
 * @author Max Karpov
 * @version 0.01
 * Valtech QA
 */

public final class Accessibility {

    private static final Logger LOGGER = Logger.getLogger(Accessibility.class);
    private static final String ACCESSIBILITY_TEST = "accessibilityTest";
    private static final String PALLY_TEXT_RESULTS = "PallyTextResults";
    private static final String PAGE_SOURCE = "PageSource";
    private static final String USER_DIRECTORY = System.getProperty("user.dir");
    private static String[] command;


    private Accessibility(){}

    /*
     * Method to run the accessibility tests on the pages behind the authentication or need session.
     * Gets the page source of the current web page pointed by the WebDriver and saves as Html file, if accessibility mode is set to true.
     * Uses Pa11y to run the accessibility tests on the saved Html file.
     * Runs the pa11y command in powershell
     * Checks for the accessibility errors in the result file if accessibility mode is set to true
     * Runs pa11y on the html file and stores the Html result in the text file.
     * Since the text file name can not contains the special characters, "//" and "/" is replaced with "." and ":" is replaced with ""
     *
     * @return true if accessibility error is found, false otherwise.
     * If accessibility mode is set to false, then returns true so that test should not fail.
     */

    public static boolean testThroughLogin() throws IOException {
        return pallyTest(true);
    }


    /*
     * Private method to get Pa11y command based on the Operating System.
     * Checks if page source needs to be saved. If yes, pa11y command is executed on the Html page saved. Otherwise calls getCommandForSimpleUrl
     * method.
     *
     * @return String array of command required for Pa11y execution.
     */

    private static String [] getCommand(boolean throughLogin){

        final String pageSourceHtmlPath;

        if (throughLogin){
            pageSourceHtmlPath = Page.savePageSourceAsHtml();
            if (!System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows")){
                command = new String[]{"/bin/bash", "-c", "pa11y --reporter html file:///" + pageSourceHtmlPath};
                LOGGER.info("Pa11y command through login:" + Arrays.toString(command));
                return command.clone();

            }else {
                command = new String[]{"powershell", "pa11y --reporter html file:///" + pageSourceHtmlPath};
                LOGGER.info("Pa11y command through login:" + Arrays.toString(command));
                return command.clone();
            }
        }else {
            return getCommandForSimpleUrl();
        }
    }

    /*
     * Private method to get Pa11y command based on the Operating System for the pages accessible without any authentication or session.
     *
     * @return String array of command required for Pa11y execution.
     */

    private static String [] getCommandForSimpleUrl(){

        if (!System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows")){
            command = new String[]{"/bin/bash", "-c", "pa11y --reporter html " + ContextManager.getDriver().getCurrentUrl()};
            LOGGER.info("pa11y command Simple Url:" + Arrays.toString(command));
            return command.clone();

        }else {
            command = new String[]{"powershell", "pa11y --reporter html " + ContextManager.getDriver().getCurrentUrl()};
            LOGGER.info("Pa11y command Simple Url:" + Arrays.toString(command));
            return command.clone();
        }
    }

    /*
     * Method to run the accessibility tests on the pages which need / do not need authentication or session.
     * If the boolean parameter 'throughLogin' is true then gets the page source of the current web page pointed by the WebDriver and saves as Html
     * file.
     * Uses Pa11y to run the accessibility tests on the saved Html file.
     * Runs the pa11y command in powershell for Windows OS and on terminal for non Windows OS.
     * Checks for the accessibility errors in the result file if accessibility mode is set to true
     * Runs pa11y on the html file.
     * Since the text file name can not contains the special characters, "//" and "/" is replaced with "." and ":" is replaced with ""
     * Logs the Pa11y command execution result in the logs.
     * Logs the Pa11y command execution result in the text file.
     *
     * @return true if accessibility error is found, false otherwise.
     * If accessibility mode is set to false, then returns false.
     */

    private static boolean pallyTest(boolean throughLogin) throws IOException {
        if (hasAccessibilityMode()) {
            ProcessBuilder builder = new ProcessBuilder(getCommand(throughLogin));
            builder.redirectErrorStream(true);

            final String pallyResultTextFilePath = USER_DIRECTORY + File.separator + PALLY_TEXT_RESULTS + File.separator +
                    ContextManager.getDriver().getCurrentUrl().replace("//", ".").replace("/", ".")
                            .replace(":","")+ ".txt";
            try(StringWriter writer = new StringWriter()){
                Process process = builder.start();
                process.waitFor();
                IOUtils.copy(process.getInputStream(), writer, StandardCharsets.UTF_8);
                final String pallyExecutionResultOnTerminal = writer.toString();
                LOGGER.info("Logging Pa11y input stream from terminal:\n" + pallyExecutionResultOnTerminal);
                File file = new File(pallyResultTextFilePath);
                FileUtils.writeStringToFile(file,pallyExecutionResultOnTerminal, StandardCharsets.UTF_8);
            }catch (InterruptedException | IOException e){
                LOGGER.error(e);
                Thread.currentThread().interrupt();
            }
            return isAccessibilityError(pallyResultTextFilePath);
        }else {
            return false;
        }
    }

    /*
     * Method to run the accessibility tests on the pages which do not need authentication or session
     * Uses Pa11y to run the accessibility tests.
     * Checks for the accessibility errors in the result file if accessibility mode is set to true
     * Runs pa11y on the current web page which WebDriver is pointing and stores the Html result in the text file.
     * Since the text file name can not contains the special characters, "//" and "/" is replaced with "." and ":" is replaced with ""
     * Calls isAccessibilityError boolean method to check for any accessibility error.
     * @throws IOException
     * @return true if accessibility error is found, false otherwise.
     */

    public static boolean testWithoutLogin() throws IOException {
        return pallyTest(false);
    }

    /*
     * Method to check if accessibility error is found in the result file.
     * Searches for "Error:" in the result text file.
     *
     * @throws FileNotFoundException if the pa11y result file is not found.
     * @param filePath Path of the text file created from pa11y result.
     * @return true if accessibility error is found, false otherwise.
     */

    private static boolean isAccessibilityError(String filePath) throws IOException {
        boolean isError = false;
        File source = new File(filePath);
        try(Scanner scan = new Scanner(source, StandardCharsets.UTF_8)){
            while(scan.hasNext()){
                final String line = scan.nextLine();
                LOGGER.info(line);
                if(line.contains("Error:")){
                    isError = true;
                    break;
                }
            }
        }
        LOGGER.info("Pa11y execution result from text file created from command:" + Arrays.toString(command));
        return isError;
    }

    /*
     * Method to merge the accessibility result files to an Html file.
     * Merges all the text files present in "PallyTestResults" folder to an Html file
     * Stores the resultant Html file to "PallyResults" folder.
     *
     */

    public static void mergeAccessibilityResults() throws IOException{
        final String sourceFilePath = USER_DIRECTORY + File.separator + PALLY_TEXT_RESULTS + File.separator;
        final String destPath = USER_DIRECTORY + File.separator + "PallyResults" + File.separator + "PallyResults" + "_" + EnvHelper.getEnvironment()+
                "_" + CommonHelper.getTimeStamp() + ".html";

        Path mergedFile = Paths.get(destPath);

        if (!mergedFile.toFile().exists()){
            Files.createFile(mergedFile);
        }

        final List<File> filesInFolder;
        try (Stream<File> fileStream = Files.walk(Paths.get(sourceFilePath)).filter(path -> path.toFile().isFile()).map(Path::toFile))  {
            filesInFolder = fileStream.collect(Collectors.toList());
        }

        final List<Path> filesPaths;
        try (Stream<Path> filePathsStream = filesInFolder.stream().map(File::toPath))  {
            filesPaths = filePathsStream.collect(Collectors.toList());
        }

        BufferedReader reader = null;
        try (BufferedWriter writer = Files.newBufferedWriter(mergedFile, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Path file : filesPaths) {
                String line;
                reader = Files.newBufferedReader(file);
                while ((line = reader.readLine()) != null) {
                    writer.append(line);
                    writer.append(System.lineSeparator());
                }
                reader.close();
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }finally {
            if (reader != null){
                reader.close();
            }
        }
    }

    /*
     * Method to delete the page source html files and Pa11y result text files.
     * Deletes the Html page source files created in "PageSource" folder.
     * Deletes the text files created in "PallyTextResults" folder.
     */

    public static void deleteFiles() {
        final String pageSourcePath = USER_DIRECTORY + File.separator + PAGE_SOURCE;
        final String pallyTextResultsPath = USER_DIRECTORY + File.separator + PALLY_TEXT_RESULTS;
        Path pageSourceDirectory = Paths.get(pageSourcePath);
        Path pallyTextResultsDirectory = Paths.get(pallyTextResultsPath);

        Arrays.stream(pageSourceDirectory.toFile().listFiles()).forEach(File::delete);
        Arrays.stream(pallyTextResultsDirectory.toFile().listFiles()).forEach(File::delete);
    }

    /*
     * Method to check mode set for accessibility testing.
     *
     * @return true or false If VM Options is not null, returns true if accessibility mode set in VM Options is true, otherwise false.
     * Otherwise returns true if the accessibility mode set in "configuration.properties file" is true, otherwise false.
     * If mode is not set in both VM Options and "configuration.properties file", returns default mode as false.
     */

    private static boolean hasAccessibilityMode(){
        final String defaultAccessibilityMode;
        BaseContext baseContext = new BaseContext();
        defaultAccessibilityMode = baseContext.getFrameworkConfiguration().getString("resources.accessibility.default");
        final String vmOptionsAccessibilityValue = System.getProperty(ACCESSIBILITY_TEST);

        final String isAccessibility;

        if (vmOptionsAccessibilityValue != null){
            isAccessibility = vmOptionsAccessibilityValue;
        }else {
            isAccessibility = ConfigHelper.getPropertyFromFile(ACCESSIBILITY_TEST,"configuration.properties",defaultAccessibilityMode);
        }

        return "true".equals(isAccessibility);
    }
}
