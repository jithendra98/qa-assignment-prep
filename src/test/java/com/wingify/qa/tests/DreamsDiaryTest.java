package com.wingify.qa.tests;

import com.wingify.qa.base.BaseTest;
import com.wingify.qa.constants.TestConstants;
import com.wingify.qa.pages.DreamsDiaryPage;
import com.wingify.qa.utils.ScreenshotUtils;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// Tests for the dreams diary page - table validation, row data, formatting
@Epic("Dream Portal QA Automation")
@Feature("Dreams Diary - dreams-diary.html")
public class DreamsDiaryTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(DreamsDiaryTest.class);
    private DreamsDiaryPage diaryPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        diaryPage = new DreamsDiaryPage(driver);
        diaryPage.navigateTo(TestConstants.DREAMS_DIARY_URL);
    }

    @Test(priority = 1, description = "Verify there are exactly 10 dream entries in the table")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Dream Table - Entry Count")
    @Description("The dream diary table should contain exactly 10 dream entries as specified.")
    public void testExactlyTenDreamEntries() {
        logger.info("=== Test: Exactly 10 dream entries ===");

        int count = diaryPage.getDreamEntryCount();
        ScreenshotUtils.captureAll(driver, "dream_diary_table");

        Assert.assertEquals(count, TestConstants.EXPECTED_TOTAL_DREAMS,
                "Dream diary should contain exactly " + TestConstants.EXPECTED_TOTAL_DREAMS + " entries");
    }

    @Test(priority = 2, description = "Verify all dream types are either 'Good' or 'Bad'")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Dream Table - Data Validation")
    @Description("Each dream entry's type should be either 'Good' or 'Bad'. No other values are acceptable.")
    public void testDreamTypesOnlyGoodOrBad() {
        logger.info("=== Test: Dream types only Good or Bad ===");

        List<String> types = diaryPage.getAllDreamTypes();
        SoftAssert softAssert = new SoftAssert();

        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            boolean isValid = type.equals(TestConstants.DREAM_TYPE_GOOD)
                    || type.equals(TestConstants.DREAM_TYPE_BAD);
            softAssert.assertTrue(isValid,
                    "Row " + (i + 1) + ": Dream type '" + type + "' is invalid. Expected 'Good' or 'Bad'");
        }

        softAssert.assertAll();
    }

    @Test(priority = 3, description = "Verify all three columns are filled in every row")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Dream Table - Data Completeness")
    @Description("Each row in the dream diary must have all three columns filled: Dream Name, Days Ago, and Dream Type.")
    public void testAllColumnsFilledInEveryRow() {
        logger.info("=== Test: All columns filled ===");

        boolean allFilled = diaryPage.areAllColumnsFilled();

        Assert.assertTrue(allFilled,
                "All three columns (Dream Name, Days Ago, Dream Type) must be filled in every row");
    }

    @Test(priority = 4, description = "Verify dream names are not empty")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Dream Table - Data Validation")
    @Description("Every dream entry should have a non-empty Dream Name.")
    public void testDreamNamesNotEmpty() {
        logger.info("=== Test: Dream names not empty ===");

        List<String> names = diaryPage.getAllDreamNames();
        SoftAssert softAssert = new SoftAssert();

        for (int i = 0; i < names.size(); i++) {
            softAssert.assertFalse(names.get(i).isEmpty(),
                    "Row " + (i + 1) + ": Dream Name should not be empty");
        }

        softAssert.assertAll();
    }

    @Test(priority = 5, description = "Verify 'Days Ago' values follow expected format")
    @Severity(SeverityLevel.NORMAL)
    @Story("Dream Table - Data Format")
    @Description("Each 'Days Ago' value should follow the format 'X day(s) ago'.")
    public void testDaysAgoFormatValid() {
        logger.info("=== Test: Days Ago format valid ===");

        List<String> daysAgoValues = diaryPage.getAllDaysAgo();
        SoftAssert softAssert = new SoftAssert();

        for (int i = 0; i < daysAgoValues.size(); i++) {
            String value = daysAgoValues.get(i);
            boolean matchesFormat = value.matches("\\d+ days? ago");
            softAssert.assertTrue(matchesFormat,
                    "Row " + (i + 1) + ": '" + value + "' doesn't match expected format 'X day(s) ago'");
        }

        softAssert.assertAll();
    }

    @Test(priority = 6, description = "Verify Days Ago values are in sequential order (1 to 10)")
    @Severity(SeverityLevel.NORMAL)
    @Story("Dream Table - Data Ordering")
    @Description("The 'Days Ago' values should be sequential from 1 to 10.")
    public void testDaysAgoSequentialOrder() {
        logger.info("=== Test: Days Ago sequential order ===");

        List<String> daysAgoValues = diaryPage.getAllDaysAgo();
        SoftAssert softAssert = new SoftAssert();

        for (int i = 0; i < daysAgoValues.size(); i++) {
            int expectedDay = i + 1;
            // Extract the number from the text
            String numStr = daysAgoValues.get(i).replaceAll("[^0-9]", "");
            if (!numStr.isEmpty()) {
                int actualDay = Integer.parseInt(numStr);
                softAssert.assertEquals(actualDay, expectedDay,
                        "Row " + (i + 1) + ": Expected " + expectedDay + " day(s) ago but found " + actualDay);
            }
        }

        softAssert.assertAll();
    }

    @Test(priority = 7, description = "Verify table headers are correct")
    @Severity(SeverityLevel.NORMAL)
    @Story("Dream Table - Structure")
    @Description("The table should have three column headers: 'Dream Name', 'Days Ago', 'Dream Type'.")
    public void testTableHeadersCorrect() {
        logger.info("=== Test: Table headers correct ===");

        List<String> headers = diaryPage.getTableHeaders();
        List<String> expectedHeaders = Arrays.asList(
                TestConstants.HEADER_DREAM_NAME,
                TestConstants.HEADER_DAYS_AGO,
                TestConstants.HEADER_DREAM_TYPE
        );

        Assert.assertEquals(headers, expectedHeaders,
                "Table headers should be: " + expectedHeaders);
    }

    @Test(priority = 8, description = "Verify page title is 'Dreams Diary'")
    @Severity(SeverityLevel.NORMAL)
    @Story("Page Metadata")
    @Description("The page title should be 'Dreams Diary'.")
    public void testPageTitle() {
        logger.info("=== Test: Page title ===");

        String title = diaryPage.getPageTitle();

        Assert.assertEquals(title, TestConstants.DIARY_PAGE_TITLE,
                "Page title should be '" + TestConstants.DIARY_PAGE_TITLE + "'");
    }

    @Test(priority = 9, description = "Verify specific known dream entries exist in the table")
    @Severity(SeverityLevel.NORMAL)
    @Story("Dream Table - Specific Entries")
    @Description("Known dream entries like 'Flying over mountains', 'Lost in maze', 'Monster chase' should exist.")
    public void testSpecificDreamEntries() {
        logger.info("=== Test: Specific dream entries exist ===");

        List<String> names = diaryPage.getAllDreamNames();
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(names.contains("Flying over mountains"),
                "'Flying over mountains' should be present in the diary");
        softAssert.assertTrue(names.contains("Lost in maze"),
                "'Lost in maze' should be present in the diary");
        softAssert.assertTrue(names.contains("Monster chase"),
                "'Monster chase' should be present in the diary");
        softAssert.assertTrue(names.contains("Winning lottery"),
                "'Winning lottery' should be present in the diary");
        softAssert.assertTrue(names.contains("Swimming with dolphins"),
                "'Swimming with dolphins' should be present in the diary");

        softAssert.assertAll();
    }
}
