package com.wingify.qa.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

/**
 * Utility class for explicit wait operations.
 * Provides reusable wait methods to handle dynamic content and loading states.
 */
public class WaitUtils {

    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver, int timeoutInSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    }

    /**
     * Waits until an element is visible on the page.
     */
    public WebElement waitForElementVisible(By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until an element is clickable.
     */
    public WebElement waitForElementClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until an element becomes invisible or is removed from DOM.
     */
    public boolean waitForElementInvisible(By locator) {
        logger.debug("Waiting for element to disappear: {}", locator);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits until an element is present in the DOM (may not be visible).
     */
    public WebElement waitForElementPresent(By locator) {
        logger.debug("Waiting for element to be present in DOM: {}", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for a specific number of windows/tabs to be open.
     */
    public boolean waitForNumberOfWindows(int expectedCount) {
        logger.debug("Waiting for {} windows/tabs to be open", expectedCount);
        return wait.until(ExpectedConditions.numberOfWindowsToBe(expectedCount));
    }

    /**
     * Waits until an element's CSS property matches the expected value.
     * Useful for checking animation states (e.g., opacity, display).
     */
    public boolean waitForCssValue(By locator, String property, String expectedValue) {
        logger.debug("Waiting for CSS property '{}' to be '{}' on: {}", property, expectedValue, locator);
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                return element.getCssValue(property).equals(expectedValue);
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                return false;
            }
        });
    }

    /**
     * Waits for page title to contain expected text.
     */
    public boolean waitForTitleContains(String titleFragment) {
        logger.debug("Waiting for page title to contain: '{}'", titleFragment);
        return wait.until(ExpectedConditions.titleContains(titleFragment));
    }

    /**
     * Custom wait with polling - waits until a condition is true.
     */
    public void waitWithPolling(int timeoutSeconds, int pollingMs, java.util.function.Supplier<Boolean> condition) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds),
                Duration.ofMillis(pollingMs));
        customWait.until(d -> condition.get());
    }
}
