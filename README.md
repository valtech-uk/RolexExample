# Java QA Framework

Find detailed information on [Confluence QA space!](https://valtech-uk.atlassian.net/wiki/spaces/QACOP/pages/1623785594/Common%2BJava%2Btest%2Bframework)

# Prerequisites

* Pa11y and Pa11y HTML reporter needs to be installed.
For Windows OS, set path for Pa11y. For example complete path up to the folder in which "pa11y.cmd" file is present.
(C:\Users\<user_name>\AppData\Roaming\npm)
* Powershell for Windows OS needs to be installed. 

* starting test page
provided npm and Node.js installed on the machine run:
```
npm install express --save
```

Make sure you have Docker running on your machine.

Once docker installed and running, cd to the project directory and run the following line:
```
docker build -t test-page-server .
```
Then run:

```
docker-compose up -d
```
then swith to /node subdirectory, and do again:

```
docker-compose up -d
```

If there were changes in docker-compose file you'll need to run the following:

```
docker-compose down
docker-compose up -d
```


This will start Selenium Grid Hub on the machine
Also, this will start test pages on the machine

# Set up
In the project directory, create the file "configuration.properties" and add the following lines which define the default browser, framework specific
config xml and path to environment specific config xml files. Environment specific configuration should be kept under configuration folder in the
project directory. As of now, this folder has 3 environment specific xml files namely config.dev.xml, config.qa.xml and config.uat.xml. These xml
files contains the environment specific configurations like base url etc. These files are kept for demonstration purpose and need to be replaced with
your actual environment specific xml files.
Also, add baseconfiguration.properties file with the same content. This file is only needed for self-test senarios.
```
mode=chrome
environment.config.path=/configuration/
framewrok.config.path=/configuration/config.xml

```

#Enable Docker Daemon
Windows:
In order to be able to start and stop Docker container please make sure that the option "Expose daemon on tcp://localhost:2375 without TLS". (Click on [Docker] icon in the taskbar, select settings> [General] and check [Expose daemon on tcp://localhost:2375 without TLS])

Mac:
Run the following in orfer to enable Docker daemon:

brew install socat
socat TCP-LISTEN:2375,reuseaddr,fork UNIX-CONNECT:/var/run/docker.sock

# Criteria for selecting environment for test execution
Priority 1. If environment is set in VM options, for example (-Denvironment = "qa"), then the test execution will be performed on qa environment.

Note: If environment is set in VM options and as well as in "configuration.properties" file, then environment set in VM options will take the
preference and the test execution will be performed on the environment set in VM options.

Priority 2. If environment is not set in VM options but set in "configuration.properties" file, for example (environment = "uat"), then the test
execution will be performed on uat environment.

Priority 3. If environment is neither set in VM options nor set in "configuration.properties" file, then the test execution will be performed on the
default environment set in the framework configuration.

If environment is not set in any of the priorities mentioned above, test execution will not be performed at all and user will be prompted to set the
environment either in VM options or in "configuration.properties" file or in framework configuration.

# Possible browser values

```
chrome
firefox
ie
edge
remote
safari
headless
proxy
```

# Safari browser setting on Mac OS

Step 1

Ensure that the Develop menu is available. It can be turned on by opening Safari preferences (Safari > Preferences in the menu bar), going to the 
Advanced tab, and ensuring that the Show Develop menu in menu bar checkbox is checked.

Step 2

Enable Remote Automation in the Develop menu. This is toggled via Develop > Allow Remote Automation in the menu bar.

Step 3

Authorize safari driver to launch the webdriver service which hosts the local web server. To permit this, run below command
```
safaridriver --enable
```

# Running Accessibility Tests

Accessibility tests execution can be enabled or disabled.

# Criteria for enabling / disabling accessibility tests
Priority 1. If "accessibilityTest" is set to "true" in VM options, for example (-DaccessibilityTest = "true"), then the accessibility tests will be 
executed. If it is set to "false", accessibility tests will not be executed.

Note: If "accessibilityTest" is set in VM options and as well as in "configuration.properties" file, then the value set in VM options will take the
preference.

Priority 2. If "accessibilityTest" is not set in VM options but set in "configuration.properties" file, for example (accessibilityTest = true), 
then the accessibility tests will be executed. If it is set to "false", accessibility tests will not be executed.

Priority 3. If "accessibilityTest" is neither set in VM options nor set in "configuration.properties" file, then based on the default value "false"
set in framework configuration, accessibility tests will not be executed.

# How to write Accessibility Tests
Class Accessibility has 2 methods methods
```
 1. public static boolean testThroughLogin()
```
If the web page on which Pa11y needs to run is accessible after user login or authentication or if the page needs any session information stored then
this method should be used. It saves the page source as an Html file and runs pa11y on the saved html file.

```
 2. public static boolean testWithoutLogin()
```
If the web page is not behind the authentication or does ot need any session information stored then this method should be used.
It runs the pa11y on the page url the WebDriver is pointing to.

Since the return type of both the methods is boolean, it returns true if the page contains any accessibility error, otherwise false.
When accessibility test execution is disabled then it returns false.


# Accessibility test reports
Accessibility test execution reports are stored in "PallyResults" folder in project directory.
Make sure that below methods are called after the accessibility tests are executed.
1. Accessibility.mergeAccessibilityResults()
2. Accessibility.deleteFiles()

# Run SonarQube

## With settings.xml
If you have configured settings.xml with your SonarQube token in 
$MAVEN_HOME/conf or ~/.m2 then you can simply run

```
mvn clean verify sonar:sonar
```

For an example configuration file see
```
docs/settings.example.xml
```

## Without settings.xml
If you have not configured a settings.xml file you can generate
the SonarQube analysis using

```
mvn clean verify sonar:sonar -Dsonar.login=<!--Your SonarQube token here -->
```

For more details see
https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/

#Reporting

There are the following pieces of reporting created by the framework:

* Html Extent report: - a high-level report containing information about all steps being run with detailed errors for the failures as well as time occurred and time elapsed values. The report destination folder depends on the entry “resources.paths.reports.cucumber-report” entry in config.xml file. The report includes screenshots taken if the following conditions are met:
The driver type is “headless” (e.g. VM options -Dmode=headless) or “remote”,
or
-Dscreenshot=true
* A comprehensive Maven report created in target/cucumber-reports directory. The report contains multiple HTML files on Feature/Scenario/step/failures levels interlinked between each other. Time elapsed provided for each step.
* .har files created in the destination depending on the entry “resources.paths.reports.cucumber-header” entry in config.xml file. The file contains http headers at the moment of the failure.
This file will be created on a failure if the driver being run is of type “proxy” (e.g. VM options -Dmode=proxy).

*	Cucumber report created at /target/cucumber/index.html file. This is a simple report to show passing/failing steps. The directory of the report depends on the plugin attribute or a runner class (e.g. plugin = “{"pretty", "html:target/cucumber", …”
*	HTML code of the failing page is saved into destination depending on the entry“resources.paths.assert-html-log” entry of config.xml file.
In order for screenshots, html file and http headers to be saved on failures implement the following (or an equivalent) in the @After clause of your step definition:

```
@After
public void clean_up(Scenario sc){
    try {
        TestHelper.completeFailure(sc);
    }finally {
        if (driver != null && System.getProperty("test") == null) {
            driver.quit();
        }

    }
}
```
