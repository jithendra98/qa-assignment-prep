package com.wingify.qa.utils;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing and managing test screenshots.
 * Supports both file-system saving and Allure report embedding.
 */
public class ScreenshotUtils {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "screenshots";

    /**
     * Captures a screenshot and saves it to the screenshots directory.
     *
     * @param driver   WebDriver instance
     * @param testName Name of the test (used in filename)
     * @return Path to the saved screenshot file
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        try {
            // Create screenshots directory if it doesn't exist
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotDir)) {
                Files.createDirectories(screenshotDir);
            }

            // Generate unique filename with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            Path filePath = screenshotDir.resolve(fileName);

            // Capture screenshot
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(filePath, screenshotBytes);

            logger.info("Screenshot saved: {}", filePath.toAbsolutePath());
            return filePath.toAbsolutePath().toString();

        } catch (IOException e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }

    /**
     * Captures a screenshot and attaches it to the Allure report.
     *
     * @param driver   WebDriver instance
     * @param stepName Name/description of the step
     */
    public static void captureAndAttachToAllure(WebDriver driver, String stepName) {
        try {
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(stepName, "image/png", new ByteArrayInputStream(screenshotBytes), ".png");
            logger.debug("Screenshot attached to Allure report: {}", stepName);
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to Allure: {}", stepName, e);
        }
    }

    /**
     * Captures a screenshot both to file system and Allure report.
     *
     * @param driver   WebDriver instance
     * @param testName Name of the test
     */
    public static void captureAll(WebDriver driver, String testName) {
        captureScreenshot(driver, testName);
        captureAndAttachToAllure(driver, testName);
    }
}
