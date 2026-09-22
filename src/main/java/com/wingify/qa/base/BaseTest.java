package com.wingify.qa.base;

import com.wingify.qa.utils.ScreenshotUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Base test class - handles WebDriver setup/teardown and config loading.
 * All test classes extend this.
 */
public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected static Properties config;
    protected WebDriver driver;

    // load config before anything runs
    @BeforeSuite(alwaysRun = true)
    public void loadConfig() {
        config = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                config.load(input);
                logger.info("Configuration loaded successfully");
            } else {
                logger.warn("config.properties not found, using defaults");
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration", e);
        }
    }

    // setup chrome driver for each test
    // TODO: maybe add firefox support later?
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        logger.info("========== Setting up WebDriver ==========");

        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Check headless mode from config
        boolean headless = Boolean.parseBoolean(config.getProperty("headless", "false"));
        if (headless) {
            options.addArguments("--headless=new");
            logger.info("Running in headless mode");
        }

        // Common Chrome options for stability
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);

        // Set timeouts
        int implicitWait = Integer.parseInt(config.getProperty("implicit.wait", "10"));
        int pageLoadTimeout = Integer.parseInt(config.getProperty("page.load.timeout", "30"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        logger.info("WebDriver initialized successfully — Browser: Chrome");
    }

    // cleanup after each test, capture screenshot if it failed
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("TEST FAILED: {}", result.getName());

            // Capture screenshot on failure
            if (driver != null) {
                ScreenshotUtils.captureAll(driver, result.getName());
                logger.info("Failure screenshot captured for: {}", result.getName());
            }

            // Attach failure details to Allure
            if (result.getThrowable() != null) {
                Allure.addAttachment("Failure Reason", result.getThrowable().getMessage());
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("TEST PASSED: {}", result.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.warn("TEST SKIPPED: {}", result.getName());
        }

        // Quit the driver
        if (driver != null) {
            driver.quit();
            logger.info("WebDriver closed successfully");
        }

        logger.info("========== Test Complete: {} ==========\n", result.getName());
    }
}
