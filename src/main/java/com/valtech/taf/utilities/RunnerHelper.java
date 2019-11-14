package com.valtech.taf.utilities;

import com.vimalselvam.cucumber.listener.ExtentProperties;

/*
 * <h1>RunnerHelper class</h1>
 * @author V. Chaudhari
 * @author M. Karpov
 */
public final class RunnerHelper {
    private static final String MASK = "\\?";
    private RunnerHelper(){}

   /*
    * <h1>setCucumberReport method </h1>
    * @param scope the actual scope of the runner class calling this method
    * @return none
    * @see this method sets ExtentProperties with the report file name
    */
    public static void setCucumberReportName(String scope){
        final String env = EnvHelper.getEnvironment();
        final String browser = DriverHelper.getBrowserName();
        final String timestamp = CommonHelper.getTimeStamp();

        final String path;

            ExtentProperties extentProperties = ExtentProperties.INSTANCE;
            path = CommonHelper.replaceValues(new BaseContext().getFrameworkConfiguration()
                            .getString("resources.paths.reports.cucumber-report"),
                    MASK,new String[]{scope,env,browser,timestamp});
            extentProperties.setReportPath(path);
            System.setProperty("report-path",path);

    }
}
