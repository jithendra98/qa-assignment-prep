package com.wingify.qa.tests;

import com.wingify.qa.base.BaseTest;
import com.wingify.qa.constants.TestConstants;
import com.wingify.qa.pages.DreamsDiaryPage;
import com.wingify.qa.pages.DreamsTotalPage;
import com.wingify.qa.utils.ScreenshotUtils;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

// Cross-page tests - checking that diary data and summary page stats are consistent
@Epic("Dream Portal QA Automation")
@Feature("Cross-Page Data Consistency Validation")
public class CrossPageValidationTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(CrossPageValidationTest.class);
    private DreamsDiaryPage diaryPage;
    private DreamsTotalPage totalPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        diaryPage = new DreamsDiaryPage(driver);
        totalPage = new DreamsTotalPage(driver);
    }

    @Test(priority = 1, description = "Cross-page: Good dream count in diary matches summary")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Data Consistency - Good Dreams")
    @Description("Count 'Good' dream entries from the diary table and compare with the Good Dreams stat in the summary page.")
    public void testGoodDreamCountMatchesDiary() {
        logger.info("=== Cross-Page Test: Good dream count consistency ===");

        // Step 1: Get count from diary page
        diaryPage.navigateTo(TestConstants.DREAMS_DIARY_URL);
        Map<String, Integer> typeCounts = diaryPage.countDreamsByType();
        int diaryGoodCount = typeCounts.getOrDefault(TestConstants.DREAM_TYPE_GOOD, 0);
        logger.info("Good dreams from diary: {}", diaryGoodCount);

        // Step 2: Get count from summary page
        totalPage.navigateTo(TestConstants.DREAMS_TOTAL_URL);
        int summaryGoodCount = totalPage.getGoodDreamsCount();
        logger.info("Good dreams from summary: {}", summaryGoodCount);

        ScreenshotUtils.captureAll(driver, "cross_page_good_dreams");

        Assert.assertEquals(diaryGoodCount, summaryGoodCount,
                "Good Dreams count from diary (" + diaryGoodCount
                        + ") should match summary page (" + summaryGoodCount + ")");
    }

    @Test(priority = 2, description = "Cross-page: Bad dream count in diary matches summary")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Data Consistency - Bad Dreams")
    @Description("Count 'Bad' dream entries from the diary table and compare with the Bad Dreams stat in the summary page.")
    public void testBadDreamCountMatchesDiary() {
        logger.info("=== Cross-Page Test: Bad dream count consistency ===");

        // Step 1: Get count from diary page
        diaryPage.navigateTo(TestConstants.DREAMS_DIARY_URL);
        Map<String, Integer> typeCounts = diaryPage.countDreamsByType();
        int diaryBadCount = typeCounts.getOrDefault(TestConstants.DREAM_TYPE_BAD, 0);

        // Step 2: Get count from summary page
        totalPage.navigateTo(TestConstants.DREAMS_TOTAL_URL);
        int summaryBadCount = totalPage.getBadDreamsCount();

        Assert.assertEquals(diaryBadCount, summaryBadCount,
                "Bad Dreams count from diary (" + diaryBadCount
                        + ") should match summary page (" + summaryBadCount + ")");
    }

    @Test(priority = 3, description = "Cross-page: Total row count in diary matches summary total")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Data Consistency - Total Dreams")
    @Description("The total number of rows in the diary table should match the Total Dreams stat in the summary page.")
    public void testTotalCountMatchesDiaryRows() {
        logger.info("=== Cross-Page Test: Total count consistency ===");

        // Step 1: Get row count from diary page
        diaryPage.navigateTo(TestConstants.DREAMS_DIARY_URL);
        int diaryRowCount = diaryPage.getDreamEntryCount();

        // Step 2: Get total from summary page
        totalPage.navigateTo(TestConstants.DREAMS_TOTAL_URL);
        int summaryTotalCount = totalPage.getTotalDreamsCount();

        Assert.assertEquals(diaryRowCount, summaryTotalCount,
                "Diary row count (" + diaryRowCount
                        + ") should match summary total (" + summaryTotalCount + ")");
    }

    @Test(priority = 4, description = "Cross-page: 'Flying over mountains' and 'Lost in maze' are recurring")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Data Consistency - Recurring Dreams")
    @Description("Verify that 'Flying over mountains' and 'Lost in maze' appear more than once in the diary, confirming they are recurring dreams.")
    public void testRecurringDreamsIdentified() {
        logger.info("=== Cross-Page Test: Recurring dreams identification ===");

        diaryPage.navigateTo(TestConstants.DREAMS_DIARY_URL);
        Map<String, Integer> recurringDreams = diaryPage.getRecurringDreams();

        logger.info("Recurring dreams found: {}", recurringDreams);

        Assert.assertTrue(recurringDreams.containsKey(TestConstants.RECURRING_DREAM_FLYING),
                "'" + TestConstants.RECURRING_DREAM_FLYING + "' should be a recurring dream. Found recurring: " + recurringDreams);

        Assert.assertTrue(recurringDreams.containsKey(TestConstants.RECURRING_DREAM_LOST),
                "'" + TestConstants.RECURRING_DREAM_LOST + "' should be a recurring dream. Found recurring: " + recurringDreams);
    }

    @Test(priority = 5, description = "Cross-page: Recurring dream count in diary matches summary")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Data Consistency - Recurring Dreams Count")
    @Description("The number of unique recurring dream names computed from the diary should match the Recurring Dreams stat in the summary.")
    public void testRecurringCountMatchesSummary() {
        logger.info("=== Cross-Page Test: Recurring count consistency ===");

        // Step 1: Compute recurring count from diary data
        diaryPage.navigateTo(TestConstants.DREAMS_DIARY_URL);
        int diaryRecurringCount = diaryPage.getRecurringDreamCount();
        logger.info("Recurring dreams from diary (computed): {}", diaryRecurringCount);

        // Step 2: Get recurring count from summary page
        totalPage.navigateTo(TestConstants.DREAMS_TOTAL_URL);
        int summaryRecurringCount = totalPage.getRecurringDreamsCount();
        logger.info("Recurring dreams from summary (displayed): {}", summaryRecurringCount);

        ScreenshotUtils.captureAll(driver, "cross_page_recurring_dreams");

        Assert.assertEquals(diaryRecurringCount, summaryRecurringCount,
                "Computed recurring count from diary (" + diaryRecurringCount
                        + ") should match summary page (" + summaryRecurringCount + ")");
    }
}
