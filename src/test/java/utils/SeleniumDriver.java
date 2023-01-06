package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import stepdefs.CommonActions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class SeleniumDriver {

    private static final ThreadLocal<WebDriver> threadLocal = new ThreadLocal<>();

    private SeleniumDriver() {

    }
    public static void setupDriver(String browser) {
        if (threadLocal.get() == null) {
            WebDriver webDriver = new ChromeDriver();
            webDriver.manage().window().maximize();
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
            threadLocal.set(webDriver);
        }
    }
    public static WebDriver getDriver(){
        return threadLocal.get();
    }

    public static void closeDriver() {
        if (threadLocal.get() != null) {
            threadLocal.get().quit();
            threadLocal.set(null);
        }
    }

    public static void attachScreenshotToReport(String name) {
        File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);

        try {
            String fileName = name + ".png";
            //FileUtils.copyFile(scrFile, new File(fileName));

            //if we need to attach file to scenario for reporting.
            CommonActions.scenarioThreadLocal.get().attach(Files.readAllBytes(Paths.get(scrFile.getAbsolutePath())), "image/png", fileName);
            System.out.println("Screenshot taken!");
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }


    //Below method takes screenshot and save to output-screenshots folder for debug
    public static void takeScreenshot() {
        File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);

        try {
            String fileName = "output-screenshots/Screenshot_" + LocalDateTime.now().toString().replace(":", "-") + ".png";
            FileUtils.copyFile(scrFile, new File(fileName));

            //if we need to attach file to scenario for reporting.
            // scenario.attach(Files.readAllBytes(Paths.get(scrFile.getAbsolutePath())),"image/png",fileName);
            System.out.println("Screenshot taken!");
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }

}
