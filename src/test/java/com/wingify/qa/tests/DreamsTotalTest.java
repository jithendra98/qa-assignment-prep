package com.wingify.qa.tests;

import com.wingify.qa.base.BaseTest;
import com.wingify.qa.constants.TestConstants;
import com.wingify.qa.pages.DreamsTotalPage;
import com.wingify.qa.utils.ScreenshotUtils;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

// Tests for the dreams summary page - verifying counts and stats
@Epic("Dream Portal QA Automation")
@Feature("Dreams Summary - dreams-total.html")
public class DreamsTotalTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(DreamsTotalTest.class);
    private DreamsTotalPage totalPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        totalPage = new DreamsTotalPage(driver);
        totalPage.navigateTo(TestConstants.DREAMS_TOTAL_URL);
    }

    @Test(priority = 1, description = "Verify Good Dreams count is 6")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Summary Statistics")
    @Description("The summary page should show Good Dreams count as 6.")
    public void testGoodDreamsCount() {
        logger.info("=== Test: Good Dreams count ===");

        int goodCount = totalPage.getGoodDreamsCount();
        ScreenshotUtils.captureAll(driver, "good_dreams_count");

        Assert.assertEquals(goodCount, TestConstants.EXPECTED_GOOD_DREAMS,
                "Good Dreams count should be " + TestConstants.EXPECTED_GOOD_DREAMS);
    }

    @Test(priority = 2, description = "Verify Bad Dreams count is 4")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Summary Statistics")
    @Description("The summary page should show Bad Dreams count as 4.")
    public void testBadDreamsCount() {
        logger.info("=== Test: Bad Dreams count ===");

        int badCount = totalPage.getBadDreamsCount();

        Assert.assertEquals(badCount, TestConstants.EXPECTED_BAD_DREAMS,
                "Bad Dreams count should be " + TestConstants.EXPECTED_BAD_DREAMS);
    }

    @Test(priority = 3, description = "Verify Total Dreams count is 10")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Summary Statistics")
    @Description("The summary page should show Total Dreams count as 10.")
    public void testTotalDreamsCount() {
        logger.info("=== Test: Total Dreams count ===");

        int totalCount = totalPage.getTotalDreamsCount();

        Assert.assertEquals(totalCount, TestConstants.EXPECTED_TOTAL_DREAMS,
                "Total Dreams count should be " + TestConstants.EXPECTED_TOTAL_DREAMS);
    }

    @Test(priority = 4, description = "Verify Recurring Dreams count is 2")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Summary Statistics")
    @Description("The summary page should show Recurring Dreams count as 2.")
    public void testRecurringDreamsCount() {
        logger.info("=== Test: Recurring Dreams count ===");

        int recurringCount = totalPage.getRecurringDreamsCount();

        Assert.assertEquals(recurringCount, TestConstants.EXPECTED_RECURRING_DREAMS,
                "Recurring Dreams count should be " + TestConstants.EXPECTED_RECURRING_DREAMS);
    }

    @Test(priority = 5, description = "Verify page title is 'Dreams Total'")
    @Severity(SeverityLevel.NORMAL)
    @Story("Page Metadata")
    @Description("The page title should be 'Dreams Total'.")
    public void testPageTitle() {
        logger.info("=== Test: Page title ===");

        String title = totalPage.getPageTitle();

        Assert.assertEquals(title, TestConstants.TOTAL_PAGE_TITLE,
                "Page title should be '" + TestConstants.TOTAL_PAGE_TITLE + "'");
    }

    @Test(priority = 6, description = "Verify page heading is 'Dreams Summary'")
    @Severity(SeverityLevel.NORMAL)
    @Story("Page Metadata")
    @Description("The page heading should be 'Dreams Summary'.")
    public void testPageHeading() {
        logger.info("=== Test: Page heading ===");

        String heading = totalPage.getPageHeading();

        Assert.assertEquals(heading, TestConstants.TOTAL_PAGE_HEADING,
                "Page heading should be '" + TestConstants.TOTAL_PAGE_HEADING + "'");
    }

    @Test(priority = 7, description = "Verify summary statistics table is displayed")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Summary Table")
    @Description("The summary statistics table should be visible on the page.")
    public void testSummaryStatsDisplayed() {
        logger.info("=== Test: Summary stats displayed ===");

        boolean isDisplayed = totalPage.isSummaryTableDisplayed();
        ScreenshotUtils.captureAll(driver, "summary_stats_displayed");

        Assert.assertTrue(isDisplayed,
                "Summary statistics table should be visible on the page");
    }

    @Test(priority = 8, description = "Verify Good + Bad Dreams equals Total Dreams")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Summary Statistics - Math Validation")
    @Description("The sum of Good Dreams and Bad Dreams should equal the Total Dreams count.")
    public void testGoodPlusBadEqualsTotal() {
        logger.info("=== Test: Good + Bad = Total ===");

        int goodCount = totalPage.getGoodDreamsCount();
        int badCount = totalPage.getBadDreamsCount();
        int totalCount = totalPage.getTotalDreamsCount();

        Assert.assertEquals(goodCount + badCount, totalCount,
                "Good Dreams (" + goodCount + ") + Bad Dreams (" + badCount
                        + ") should equal Total Dreams (" + totalCount + ")");
    }
}
