package com.wingify.qa.pages;

import com.wingify.qa.utils.WaitUtils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// Page object for the home page (index.html)
// Has a loading animation that disappears after ~3 seconds, then shows the dream button
public class DreamPortalHomePage {

    private static final Logger logger = LogManager.getLogger(DreamPortalHomePage.class);

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    // ==================== Locators ====================
    private final By loadingAnimation = By.id("loadingAnimation");
    private final By mainContent = By.id("mainContent");
    private final By contentWrapper = By.cssSelector(".content-wrapper");
    private final By dreamButton = By.id("dreamButton");
    private final By pageHeading = By.cssSelector("h1");

    // ==================== Constructor ====================
    public DreamPortalHomePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver, 10);
    }

    // ==================== Page Actions ====================

    /**
     * Navigates to the Dream Portal home page.
     */
    @Step("Navigate to Dream Portal Home Page")
    public DreamPortalHomePage navigateTo(String url) {
        logger.info("Navigating to Dream Portal Home: {}", url);
        driver.get(url);
        return this;
    }

    // check if loading overlay is showing
    @Step("Check if loading animation is visible")
    public boolean isLoadingAnimationVisible() {
        try {
            WebElement overlay = driver.findElement(loadingAnimation);
            boolean visible = overlay.isDisplayed();
            logger.info("Loading animation visible: {}", visible);
            return visible;
        } catch (Exception e) {
            logger.debug("Loading animation not found in DOM — may have already been removed");
            return false;
        }
    }

    // wait until the loading spinner goes away
    @Step("Wait for loading animation to disappear")
    public boolean waitForLoadingToDisappear() {
        logger.info("Waiting for loading animation to disappear...");
        try {
            boolean disappeared = waitUtils.waitForElementInvisible(loadingAnimation);
            logger.info("Loading animation disappeared: {}", disappeared);
            return disappeared;
        } catch (Exception e) {
            logger.warn("Loading animation did not disappear within timeout");
            return false;
        }
    }

    /**
     * Checks if the main content container is visible after loading.
     */
    @Step("Check if main content is visible")
    public boolean isMainContentVisible() {
        try {
            WebElement content = waitUtils.waitForElementVisible(mainContent);
            boolean visible = content.isDisplayed();
            logger.info("Main content visible: {}", visible);
            return visible;
        } catch (Exception e) {
            logger.error("Main content not visible", e);
            return false;
        }
    }

    /**
     * Checks if the "My Dreams" button is displayed and enabled.
     */
    @Step("Check if 'My Dreams' button is visible")
    public boolean isDreamButtonVisible() {
        try {
            WebElement button = waitUtils.waitForElementVisible(dreamButton);
            boolean visible = button.isDisplayed();
            logger.info("'My Dreams' button visible: {}", visible);
            return visible;
        } catch (Exception e) {
            logger.error("'My Dreams' button not visible", e);
            return false;
        }
    }

    /**
     * Clicks the "My Dreams" button which opens diary and summary pages in new tabs.
     */
    @Step("Click 'My Dreams' button")
    public void clickDreamButton() {
        logger.info("Clicking 'My Dreams' button...");
        WebElement button = waitUtils.waitForElementClickable(dreamButton);
        button.click();
        logger.info("'My Dreams' button clicked successfully");
    }

    /**
     * Returns the page title.
     */
    @Step("Get page title")
    public String getPageTitle() {
        String title = driver.getTitle();
        logger.info("Page title: {}", title);
        return title;
    }

    /**
     * Returns the text of the "My Dreams" button.
     */
    @Step("Get 'My Dreams' button text")
    public String getDreamButtonText() {
        WebElement button = waitUtils.waitForElementVisible(dreamButton);
        String text = button.getText();
        logger.info("Dream button text: '{}'", text);
        return text;
    }
}
