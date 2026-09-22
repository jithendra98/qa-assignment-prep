package com.wingify.qa.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for managing multiple browser windows and tabs.
 * Handles tab switching, new tab detection, and URL retrieval across tabs.
 */
public class WindowUtils {

    private static final Logger logger = LogManager.getLogger(WindowUtils.class);

    private final WebDriver driver;

    public WindowUtils(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Returns the current number of open windows/tabs.
     */
    public int getWindowCount() {
        int count = driver.getWindowHandles().size();
        logger.debug("Current window/tab count: {}", count);
        return count;
    }

    /**
     * Switches to a window/tab by its index (0-based).
     */
    public void switchToWindow(int index) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (index >= 0 && index < handles.size()) {
            driver.switchTo().window(handles.get(index));
            logger.info("Switched to window index {}: {}", index, driver.getCurrentUrl());
        } else {
            logger.error("Invalid window index: {}. Total windows: {}", index, handles.size());
            throw new IndexOutOfBoundsException("Window index " + index + " out of range. Total: " + handles.size());
        }
    }

    /**
     * Returns URLs of all currently open windows/tabs.
     */
    public List<String> getAllWindowUrls() {
        List<String> urls = new ArrayList<>();
        String originalHandle = driver.getWindowHandle();

        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            urls.add(driver.getCurrentUrl());
        }

        // Switch back to the original window
        driver.switchTo().window(originalHandle);
        logger.info("All window URLs: {}", urls);
        return urls;
    }

    /**
     * Switches to a window/tab that contains the specified URL fragment.
     *
     * @param urlFragment Part of the URL to match
     * @return true if a matching window was found and switched to
     */
    public boolean switchToWindowByUrl(String urlFragment) {
        String originalHandle = driver.getWindowHandle();

        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getCurrentUrl().contains(urlFragment)) {
                logger.info("Switched to window with URL containing '{}': {}", urlFragment, driver.getCurrentUrl());
                return true;
            }
        }

        // If not found, switch back to original
        driver.switchTo().window(originalHandle);
        logger.warn("No window found with URL containing: {}", urlFragment);
        return false;
    }

    /**
     * Closes all tabs except the original (first) one.
     */
    public void closeAllExceptFirst() {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        String firstHandle = handles.get(0);

        for (int i = handles.size() - 1; i > 0; i--) {
            driver.switchTo().window(handles.get(i));
            driver.close();
            logger.debug("Closed tab index {}", i);
        }

        driver.switchTo().window(firstHandle);
        logger.info("Closed all tabs except the first one");
    }
}
