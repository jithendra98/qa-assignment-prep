package com.wingify.qa.tests;

import com.wingify.qa.base.BaseTest;
import com.wingify.qa.constants.TestConstants;
import com.wingify.qa.pages.DreamPortalHomePage;
import com.wingify.qa.utils.ScreenshotUtils;
import com.wingify.qa.utils.WaitUtils;
import com.wingify.qa.utils.WindowUtils;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

// Tests for the home page (index.html) - loading animation, button, tab navigation
@Epic("Dream Portal QA Automation")
@Feature("Home Page - index.html")
public class HomePageTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(HomePageTest.class);
    private DreamPortalHomePage homePage;
    private WindowUtils windowUtils;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        homePage = new DreamPortalHomePage(driver);
        windowUtils = new WindowUtils(driver);
    }

    @Test(priority = 1, description = "Verify loading animation appears on page load")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Loading Animation")
    @Description("The loading animation overlay should be visible immediately when the page loads.")
    public void testLoadingAnimationAppears() {
        logger.info("=== Test: Loading animation appears ===");
        driver.get(TestConstants.BASE_URL);

        boolean overlayVisible = homePage.isLoadingAnimationVisible();
        ScreenshotUtils.captureAll(driver, "loading_animation_visible");

        Assert.assertTrue(overlayVisible,
                "Loading animation overlay should be visible when the page first loads");
    }

    @Test(priority = 2, description = "Verify loading animation disappears after ~3 seconds")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Loading Animation")
    @Description("The loading animation should disappear within approximately 3-5 seconds, revealing the main content.")
    public void testLoadingAnimationDisappears() {
        logger.info("=== Test: Loading animation disappears ===");
        driver.get(TestConstants.BASE_URL);

        boolean disappeared = homePage.waitForLoadingToDisappear();
        ScreenshotUtils.captureAll(driver, "loading_animation_disappeared");

        Assert.assertTrue(disappeared,
                "Loading animation should disappear within the timeout period (~3-5 seconds)");
    }

    @Test(priority = 3, description = "Verify main content is visible after loading completes")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Content Visibility")
    @Description("After the loading animation disappears, the main content container should become visible.")
    public void testMainContentVisibleAfterLoading() {
        logger.info("=== Test: Main content visible after loading ===");
        driver.get(TestConstants.BASE_URL);

        // Wait for loading to complete
        homePage.waitForLoadingToDisappear();

        boolean contentVisible = homePage.isMainContentVisible();
        ScreenshotUtils.captureAll(driver, "main_content_visible");

        Assert.assertTrue(contentVisible,
                "Main content should be visible after loading animation completes");
    }

    @Test(priority = 4, description = "Verify 'My Dreams' button is visible and clickable after loading")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Content Visibility")
    @Description("The 'My Dreams' button should be displayed and interactive after the loading animation completes.")
    public void testDreamButtonVisible() {
        logger.info("=== Test: Dream button visible ===");
        driver.get(TestConstants.BASE_URL);

        homePage.waitForLoadingToDisappear();

        boolean buttonVisible = homePage.isDreamButtonVisible();
        ScreenshotUtils.captureAll(driver, "dream_button_visible");

        Assert.assertTrue(buttonVisible,
                "'My Dreams' button should be visible after loading completes");
    }

    @Test(priority = 5, description = "Verify clicking 'My Dreams' opens exactly 2 new tabs")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigation - My Dreams Button")
    @Description("Clicking the 'My Dreams' button should open two new browser tabs: one for the diary and one for the summary.")
    public void testDreamButtonOpensNewTabs() {
        logger.info("=== Test: Dream button opens new tabs ===");
        driver.get(TestConstants.BASE_URL);

        homePage.waitForLoadingToDisappear();

        // Verify only 1 tab before clicking
        Assert.assertEquals(windowUtils.getWindowCount(), 1,
                "Should have exactly 1 tab before clicking the button");

        homePage.clickDreamButton();

        // Wait for new tabs to open
        WaitUtils waitUtil = new WaitUtils(driver, TestConstants.NEW_TAB_WAIT);
        waitUtil.waitForNumberOfWindows(TestConstants.EXPECTED_TAB_COUNT_AFTER_CLICK);

        int tabCount = windowUtils.getWindowCount();
        ScreenshotUtils.captureAll(driver, "new_tabs_opened");

        Assert.assertEquals(tabCount, TestConstants.EXPECTED_TAB_COUNT_AFTER_CLICK,
                "Clicking 'My Dreams' should open exactly 2 new tabs (total 3)");
    }

    @Test(priority = 6, description = "Verify dreams-diary.html is opened in one of the new tabs")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigation - My Dreams Button")
    @Description("One of the new tabs should navigate to dreams-diary.html.")
    public void testDreamDiaryTabOpened() {
        logger.info("=== Test: Dream diary tab opened ===");
        driver.get(TestConstants.BASE_URL);

        homePage.waitForLoadingToDisappear();
        homePage.clickDreamButton();

        WaitUtils waitUtil = new WaitUtils(driver, TestConstants.NEW_TAB_WAIT);
        waitUtil.waitForNumberOfWindows(TestConstants.EXPECTED_TAB_COUNT_AFTER_CLICK);

        List<String> urls = windowUtils.getAllWindowUrls();
        logger.info("Open tab URLs: {}", urls);

        boolean diaryTabFound = urls.stream()
                .anyMatch(url -> url.contains("dreams-diary.html"));

        Assert.assertTrue(diaryTabFound,
                "One of the new tabs should contain dreams-diary.html. Open URLs: " + urls);
    }

    @Test(priority = 7, description = "Verify dreams-total.html is opened in one of the new tabs")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigation - My Dreams Button")
    @Description("One of the new tabs should navigate to dreams-total.html.")
    public void testDreamTotalTabOpened() {
        logger.info("=== Test: Dream total tab opened ===");
        driver.get(TestConstants.BASE_URL);

        homePage.waitForLoadingToDisappear();
        homePage.clickDreamButton();

        WaitUtils waitUtil = new WaitUtils(driver, TestConstants.NEW_TAB_WAIT);
        waitUtil.waitForNumberOfWindows(TestConstants.EXPECTED_TAB_COUNT_AFTER_CLICK);

        List<String> urls = windowUtils.getAllWindowUrls();
        logger.info("Open tab URLs: {}", urls);

        boolean totalTabFound = urls.stream()
                .anyMatch(url -> url.contains("dreams-total.html"));

        Assert.assertTrue(totalTabFound,
                "One of the new tabs should contain dreams-total.html. Open URLs: " + urls);
    }

    @Test(priority = 8, description = "Verify page title is 'Dream Portal'")
    @Severity(SeverityLevel.NORMAL)
    @Story("Page Metadata")
    @Description("The page title should be 'Dream Portal'.")
    public void testPageTitle() {
        logger.info("=== Test: Page title ===");
        driver.get(TestConstants.BASE_URL);

        String title = homePage.getPageTitle();

        Assert.assertEquals(title, TestConstants.HOME_PAGE_TITLE,
                "Page title should be 'Dream Portal'");
    }
}
