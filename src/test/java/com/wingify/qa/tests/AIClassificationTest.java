package com.wingify.qa.tests;

import com.wingify.qa.ai.DreamClassifier;
import com.wingify.qa.base.BaseTest;
import com.wingify.qa.constants.TestConstants;
import com.wingify.qa.pages.DreamsDiaryPage;
import com.wingify.qa.utils.ScreenshotUtils;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// AI classification tests (bonus part 2)
// Classifies dreams using OpenAI and compares with the diary table data
// Falls back to keyword classifier if no API key is available
@Epic("Dream Portal QA Automation")
@Feature("AI-Based Dream Classification (Bonus)")
public class AIClassificationTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(AIClassificationTest.class);
    private DreamsDiaryPage diaryPage;
    private DreamClassifier classifier;
    private boolean aiEnabled = false;

    @BeforeClass(alwaysRun = true)
    public void initAI() {
        // Try to get API key from environment variable first, then config
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = config.getProperty("openai.api.key", "");
        }

        String model = config.getProperty("openai.model", "gpt-3.5-turbo");
        classifier = new DreamClassifier(apiKey, model);
        aiEnabled = Boolean.parseBoolean(config.getProperty("ai.enabled", "true"));

        logger.info("AI Classification is ENABLED — Active Engine: {}", classifier.getEngineMode());
    }

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        diaryPage = new DreamsDiaryPage(driver);
    }

    /**
     * Skips the test if explicitly disabled in config.
     */
    private void skipIfAIDisabled() {
        if (!aiEnabled) {
            throw new SkipException("AI tests are explicitly disabled via config.properties (ai.enabled=false).");
        }
    }

    @Test(priority = 1, description = "AI: Classify 'Monster chase' as Bad")
    @Severity(SeverityLevel.NORMAL)
    @Story("AI Dream Classification - Individual")
    @Description("Use OpenAI/NLP to classify 'Monster chase' — expected result: 'Bad'.")
    public void testMonsterChaseClassifiedAsBad() {
        skipIfAIDisabled();

        logger.info("=== AI Test: Monster chase classification ===");

        String classification = classifier.classifyDream("Monster chase");

        Assert.assertEquals(classification, TestConstants.DREAM_TYPE_BAD,
                "AI should classify 'Monster chase' as 'Bad', but got: " + classification);
    }

    @Test(priority = 2, description = "AI: Classify 'Flying over mountains' as Good")
    @Severity(SeverityLevel.NORMAL)
    @Story("AI Dream Classification - Individual")
    @Description("Use OpenAI/NLP to classify 'Flying over mountains' — expected result: 'Good'.")
    public void testFlyingOverMountainsClassifiedAsGood() {
        skipIfAIDisabled();

        logger.info("=== AI Test: Flying over mountains classification ===");

        String classification = classifier.classifyDream("Flying over mountains");

        Assert.assertEquals(classification, TestConstants.DREAM_TYPE_GOOD,
                "AI should classify 'Flying over mountains' as 'Good', but got: " + classification);
    }

    @Test(priority = 3, description = "AI: Classify 'Lost in maze' as Bad")
    @Severity(SeverityLevel.NORMAL)
    @Story("AI Dream Classification - Individual")
    @Description("Use OpenAI/NLP to classify 'Lost in maze' — expected result: 'Bad'.")
    public void testLostInMazeClassifiedAsBad() {
        skipIfAIDisabled();

        logger.info("=== AI Test: Lost in maze classification ===");

        String classification = classifier.classifyDream("Lost in maze");

        Assert.assertEquals(classification, TestConstants.DREAM_TYPE_BAD,
                "AI should classify 'Lost in maze' as 'Bad', but got: " + classification);
    }

    @Test(priority = 4, description = "AI: Compare AI classification with diary table for all dreams")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AI vs Table - Full Comparison")
    @Description("Retrieve all dream names from the diary, classify each using AI/NLP, and compare with the actual Dream Type in the table.")
    public void testAllDreamsAIVsTableComparison() {
        skipIfAIDisabled();

        logger.info("=== AI Test: Full comparison — AI vs Table ===");

        // Step 1: Get all dream data from the diary
        diaryPage.navigateTo(TestConstants.DREAMS_DIARY_URL);
        List<Map<String, String>> tableData = diaryPage.getCompleteTableData();
        ScreenshotUtils.captureAll(driver, "ai_comparison_diary");

        SoftAssert softAssert = new SoftAssert();
        int matchCount = 0;
        int totalDreams = tableData.size();

        // Step 2: Classify each dream and compare
        StringBuilder report = new StringBuilder();
        report.append("\n╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║           AI vs Table — Dream Classification Comparison         ║\n");
        report.append("╠══════════════════════════════════════════════════════════════════╣\n");
        report.append(String.format("║ %-30s │ %-8s │ %-8s │ %-6s ║%n",
                "Dream Name", "Table", "AI", "Match?"));
        report.append("╠══════════════════════════════════════════════════════════════════╣\n");

        for (Map<String, String> entry : tableData) {
            String dreamName = entry.get("Dream Name");
            String tableType = entry.get("Dream Type");

            String aiClassification = classifier.classifyDream(dreamName);

            boolean matches = tableType.equals(aiClassification);
            if (matches) matchCount++;

            String matchSymbol = matches ? "  ✓  " : "  ✗  ";
            report.append(String.format("║ %-30s │ %-8s │ %-8s │ %s ║%n",
                    dreamName, tableType, aiClassification, matchSymbol));

            softAssert.assertEquals(aiClassification, tableType,
                    "AI classification mismatch for '" + dreamName
                            + "': AI said '" + aiClassification + "' but table says '" + tableType + "'");
        }

        report.append("╠══════════════════════════════════════════════════════════════════╣\n");
        report.append(String.format("║ Accuracy: %d/%d (%.1f%%)                                        ║%n",
                matchCount, totalDreams, (matchCount * 100.0) / totalDreams));
        report.append("╚══════════════════════════════════════════════════════════════════╝\n");

        logger.info(report.toString());

        // Attach the comparison report to Allure
        Allure.addAttachment("AI vs Table Comparison Report", "text/plain", report.toString());

        softAssert.assertAll();
    }
}
