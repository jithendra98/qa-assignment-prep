package com.wingify.qa.pages;

import com.wingify.qa.utils.WaitUtils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Page Object Model for the Dreams Total/Summary Page (dreams-total.html).
 *
 * This page displays summary statistics of the dream journal:
 * - Good Dreams count
 * - Bad Dreams count
 * - Total Dreams count
 * - Recurring Dreams count
 */
public class DreamsTotalPage {

    private static final Logger logger = LogManager.getLogger(DreamsTotalPage.class);

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    // ==================== Locators ====================
    private final By summaryTable = By.id("dreamsTotal");
    private final By tableRows = By.cssSelector("#dreamsTotal tbody tr");
    private final By pageHeading = By.cssSelector("h1");
    private final By statLabels = By.cssSelector("#dreamsTotal tbody tr td:nth-child(1)");
    private final By statValues = By.cssSelector("#dreamsTotal tbody tr td:nth-child(2)");

    // ==================== Constructor ====================
    public DreamsTotalPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver, 10);
    }

    // ==================== Page Actions ====================

    /**
     * Navigates directly to the Dreams Total page.
     */
    @Step("Navigate to Dreams Total Page")
    public DreamsTotalPage navigateTo(String url) {
        logger.info("Navigating to Dreams Total: {}", url);
        driver.get(url);
        waitUtils.waitForElementVisible(summaryTable);
        return this;
    }

    /**
     * Returns the page title.
     */
    @Step("Get page title")
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Returns the page heading text.
     */
    @Step("Get page heading")
    public String getPageHeading() {
        WebElement heading = waitUtils.waitForElementVisible(pageHeading);
        return heading.getText().trim();
    }

    /**
     * Checks if the summary table is visible.
     */
    @Step("Check if summary table is displayed")
    public boolean isSummaryTableDisplayed() {
        try {
            WebElement table = waitUtils.waitForElementVisible(summaryTable);
            return table.isDisplayed();
        } catch (Exception e) {
            logger.error("Summary table not visible", e);
            return false;
        }
    }

    /**
     * Returns all summary statistics as a map of label → value.
     * Example: {"Good Dreams" → 6, "Bad Dreams" → 4, "Total Dreams" → 10}
     */
    @Step("Get all summary statistics")
    public Map<String, Integer> getAllStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        List<WebElement> rows = driver.findElements(tableRows);

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 2) {
                String label = cells.get(0).getText().trim();
                String valueText = cells.get(1).getText().trim();
                try {
                    int value = Integer.parseInt(valueText);
                    stats.put(label, value);
                } catch (NumberFormatException e) {
                    logger.warn("Non-numeric stat value for '{}': '{}'", label, valueText);
                }
            }
        }

        logger.info("Summary statistics: {}", stats);
        return stats;
    }

    /**
     * Returns the value for a specific stat by its label.
     *
     * @param label The label text (e.g., "Good Dreams")
     * @return The integer value, or -1 if not found
     */
    @Step("Get stat value for: {label}")
    public int getStatValue(String label) {
        Map<String, Integer> stats = getAllStats();

        // Try exact match first
        if (stats.containsKey(label)) {
            return stats.get(label);
        }

        // Try case-insensitive partial match
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            if (entry.getKey().toLowerCase().contains(label.toLowerCase())) {
                logger.info("Stat '{}' = {}", label, entry.getValue());
                return entry.getValue();
            }
        }

        logger.warn("Stat label not found: '{}'", label);
        return -1;
    }

    /**
     * Returns the Good Dreams count.
     */
    @Step("Get Good Dreams count")
    public int getGoodDreamsCount() {
        return getStatValue("Good");
    }

    /**
     * Returns the Bad Dreams count.
     */
    @Step("Get Bad Dreams count")
    public int getBadDreamsCount() {
        return getStatValue("Bad");
    }

    /**
     * Returns the Total Dreams count.
     */
    @Step("Get Total Dreams count")
    public int getTotalDreamsCount() {
        return getStatValue("Total");
    }

    /**
     * Returns the Recurring Dreams count.
     */
    @Step("Get Recurring Dreams count")
    public int getRecurringDreamsCount() {
        return getStatValue("Recurring");
    }

    /**
     * Returns the number of stat rows displayed.
     */
    @Step("Get number of stat rows")
    public int getStatRowCount() {
        List<WebElement> rows = driver.findElements(tableRows);
        return rows.size();
    }
}
