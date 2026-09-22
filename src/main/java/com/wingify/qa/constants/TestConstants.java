package com.wingify.qa.constants;

// Constants used across all test classes
public final class TestConstants {

    private TestConstants() {} // utility class

    // ==================== URLs ====================
    public static final String BASE_URL = "https://arjitnigam.github.io/myDreams/";
    public static final String DREAMS_DIARY_URL = BASE_URL + "dreams-diary.html";
    public static final String DREAMS_TOTAL_URL = BASE_URL + "dreams-total.html";

    // ==================== Page Titles ====================
    public static final String HOME_PAGE_TITLE = "Dream Portal";
    public static final String DIARY_PAGE_TITLE = "Dreams Diary";
    public static final String TOTAL_PAGE_TITLE = "Dreams Total";

    // ==================== Page Headings ====================
    public static final String DIARY_PAGE_HEADING = "Dreams Diary";
    public static final String TOTAL_PAGE_HEADING = "Dreams Summary";

    // ==================== Expected Dream Data ====================
    public static final int EXPECTED_TOTAL_DREAMS = 10;
    public static final int EXPECTED_GOOD_DREAMS = 6;
    public static final int EXPECTED_BAD_DREAMS = 4;
    public static final int EXPECTED_RECURRING_DREAMS = 2;

    // ==================== Dream Types ====================
    public static final String DREAM_TYPE_GOOD = "Good";
    public static final String DREAM_TYPE_BAD = "Bad";

    // ==================== Recurring Dream Names ====================
    public static final String RECURRING_DREAM_FLYING = "Flying over mountains";
    public static final String RECURRING_DREAM_LOST = "Lost in maze";

    // ==================== Table Headers ====================
    public static final String HEADER_DREAM_NAME = "Dream Name";
    public static final String HEADER_DAYS_AGO = "Days Ago";
    public static final String HEADER_DREAM_TYPE = "Dream Type";

    // ==================== Timeouts ====================
    public static final int LOADING_ANIMATION_TIMEOUT = 5;
    public static final int PAGE_LOAD_TIMEOUT = 10;
    public static final int NEW_TAB_WAIT = 3;

    // ==================== Expected Tab Count ====================
    public static final int EXPECTED_TAB_COUNT_AFTER_CLICK = 3;
}
