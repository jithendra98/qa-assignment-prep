package com.wingify.qa.pages;

import com.wingify.qa.utils.WaitUtils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;

/**
 * Page Object Model for the Dreams Diary Page (dreams-diary.html).
 *
 * This page displays a table of dream entries with columns:
 * - Dream Name
 * - Days Ago
 * - Dream Type (Good/Bad)
 */
public class DreamsDiaryPage {

    private static final Logger logger = LogManager.getLogger(DreamsDiaryPage.class);

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    // ==================== Locators ====================
    private final By dreamTable = By.id("dreamsDiary");
    private final By tableHeaders = By.cssSelector("#dreamsDiary thead th");
    private final By tableRows = By.cssSelector("#dreamsDiary tbody tr");
    private final By pageHeading = By.cssSelector("h1");
    private final By allDreamNames = By.cssSelector("#dreamsDiary tbody tr td:nth-child(1)");
    private final By allDaysAgo = By.cssSelector("#dreamsDiary tbody tr td:nth-child(2)");
    private final By allDreamTypes = By.cssSelector("#dreamsDiary tbody tr td:nth-child(3)");

    // ==================== Constructor ====================
    public DreamsDiaryPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver, 10);
    }

    // ==================== Page Actions ====================

    /**
     * Navigates directly to the Dreams Diary page.
     */
    @Step("Navigate to Dreams Diary Page")
    public DreamsDiaryPage navigateTo(String url) {
        logger.info("Navigating to Dreams Diary: {}", url);
        driver.get(url);
        waitUtils.waitForElementVisible(dreamTable);
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
        return heading.getText();
    }

    /**
     * Returns the total number of dream entries (rows) in the table.
     */
    @Step("Get total number of dream entries")
    public int getDreamEntryCount() {
        List<WebElement> rows = driver.findElements(tableRows);
        int count = rows.size();
        logger.info("Total dream entries found: {}", count);
        return count;
    }

    /**
     * Returns a list of all table header texts.
     */
    @Step("Get table headers")
    public List<String> getTableHeaders() {
        List<WebElement> headers = driver.findElements(tableHeaders);
        List<String> headerTexts = new ArrayList<>();
        for (WebElement header : headers) {
            headerTexts.add(header.getText().trim());
        }
        logger.info("Table headers: {}", headerTexts);
        return headerTexts;
    }

    /**
     * Returns all dream names from the table.
     */
    @Step("Get all dream names")
    public List<String> getAllDreamNames() {
        List<WebElement> nameElements = driver.findElements(allDreamNames);
        List<String> names = new ArrayList<>();
        for (WebElement element : nameElements) {
            names.add(element.getText().trim());
        }
        logger.info("Dream names: {}", names);
        return names;
    }

    /**
     * Returns all "Days Ago" values from the table.
     */
    @Step("Get all 'Days Ago' values")
    public List<String> getAllDaysAgo() {
        List<WebElement> daysElements = driver.findElements(allDaysAgo);
        List<String> days = new ArrayList<>();
        for (WebElement element : daysElements) {
            days.add(element.getText().trim());
        }
        logger.info("Days ago values: {}", days);
        return days;
    }

    /**
     * Returns all dream types (Good/Bad) from the table.
     */
    @Step("Get all dream types")
    public List<String> getAllDreamTypes() {
        List<WebElement> typeElements = driver.findElements(allDreamTypes);
        List<String> types = new ArrayList<>();
        for (WebElement element : typeElements) {
            types.add(element.getText().trim());
        }
        logger.info("Dream types: {}", types);
        return types;
    }

    /**
     * Returns the complete table data as a list of maps.
     * Each map represents a row with keys: "Dream Name", "Days Ago", "Dream Type".
     */
    @Step("Get complete table data")
    public List<Map<String, String>> getCompleteTableData() {
        List<Map<String, String>> tableData = new ArrayList<>();
        List<WebElement> rows = driver.findElements(tableRows);

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 3) {
                Map<String, String> rowData = new LinkedHashMap<>();
                rowData.put("Dream Name", cells.get(0).getText().trim());
                rowData.put("Days Ago", cells.get(1).getText().trim());
                rowData.put("Dream Type", cells.get(2).getText().trim());
                tableData.add(rowData);
            }
        }

        logger.info("Complete table data retrieved: {} rows", tableData.size());
        return tableData;
    }

    /**
     * Checks if all cells in every row are non-empty.
     *
     * @return true if all cells contain data
     */
    @Step("Verify all columns are filled in every row")
    public boolean areAllColumnsFilled() {
        List<Map<String, String>> data = getCompleteTableData();
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    logger.error("Empty cell found at row {} column '{}'", i + 1, entry.getKey());
                    return false;
                }
            }
        }
        logger.info("All columns are filled in every row");
        return true;
    }

    /**
     * Returns a count of each dream type (Good/Bad) from the table.
     */
    @Step("Count dreams by type")
    public Map<String, Integer> countDreamsByType() {
        List<String> types = getAllDreamTypes();
        Map<String, Integer> countMap = new HashMap<>();
        for (String type : types) {
            countMap.merge(type, 1, Integer::sum);
        }
        logger.info("Dreams count by type: {}", countMap);
        return countMap;
    }

    /**
     * Identifies recurring dreams (names that appear more than once).
     *
     * @return Map of dream name to occurrence count (only recurring)
     */
    @Step("Identify recurring dreams")
    public Map<String, Integer> getRecurringDreams() {
        List<String> names = getAllDreamNames();
        Map<String, Integer> countMap = new HashMap<>();

        // Count occurrences of each dream name
        for (String name : names) {
            countMap.merge(name, 1, Integer::sum);
        }

        // Filter to only recurring (count > 1)
        Map<String, Integer> recurring = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > 1) {
                recurring.put(entry.getKey(), entry.getValue());
            }
        }

        logger.info("Recurring dreams identified: {}", recurring);
        return recurring;
    }

    /**
     * Returns the count of unique recurring dream names.
     */
    @Step("Get recurring dream count")
    public int getRecurringDreamCount() {
        return getRecurringDreams().size();
    }
}
