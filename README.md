# Dream Portal - QA Automation Suite

Automated test suite for the [Dream Portal](https://arjitnigam.github.io/myDreams/) web application using Selenium WebDriver, Java, TestNG, and Allure Reports. Built using the Page Object Model (POM) design pattern.

## Tech Stack

- Java 17+
- Maven 3.9+
- Selenium WebDriver 4.27.0
- TestNG 7.10.2
- WebDriverManager 5.9.2
- Allure Reports 2.29.0
- Log4j2 for logging
- OpenAI API (for bonus AI classification)

## Project Structure

```
src/
├── main/java/com/wingify/qa/
│   ├── base/
│   │   └── BaseTest.java              # WebDriver setup and teardown
│   ├── pages/
│   │   ├── DreamPortalHomePage.java   # Home page actions
│   │   ├── DreamsDiaryPage.java       # Diary table operations
│   │   └── DreamsTotalPage.java       # Summary page operations
│   ├── utils/
│   │   ├── WaitUtils.java             # Explicit wait helpers
│   │   ├── ScreenshotUtils.java       # Screenshot on failure
│   │   └── WindowUtils.java           # Multi-tab handling
│   ├── ai/
│   │   └── DreamClassifier.java       # AI dream classification
│   └── constants/
│       └── TestConstants.java         # Test data and URLs
│
├── test/java/com/wingify/qa/tests/
│   ├── HomePageTest.java              # 8 tests
│   ├── DreamsDiaryTest.java           # 9 tests
│   ├── DreamsTotalTest.java           # 8 tests
│   ├── CrossPageValidationTest.java   # 5 tests
│   └── AIClassificationTest.java      # 4 tests
│
└── test/resources/
    ├── config.properties
    ├── log4j2.xml
    └── allure.properties
```

## Test Coverage

### Part 1 - Functional Tests (30 tests)

**Home Page (index.html)** - Loading animation, button visibility, navigation to diary and summary tabs, page title

**Dreams Diary (dreams-diary.html)** - 10 entries validation, dream types (Good/Bad only), column completeness, days ago format and order, table headers, specific entries

**Dreams Summary (dreams-total.html)** - Good=6, Bad=4, Total=10, Recurring=2, stats table visibility, Good+Bad=Total math check

**Cross-Page Validation** - Diary Good count matches summary, Bad count matches, total rows match, recurring dreams identified from diary data

### Part 2 - AI Classification (4 tests)

Uses OpenAI API to classify dreams and compare with table data. Falls back to a keyword-based classifier when no API key is available.

- Individual classification tests for known dreams
- Full table comparison with accuracy report

## Setup

```bash
# Check prerequisites
java -version    # needs 17+
mvn -version     # needs 3.9+

# Install dependencies
mvn clean install -DskipTests

# (Optional) Set OpenAI API key for AI tests
set OPENAI_API_KEY=your-key-here
```

## Running Tests

```bash
# Run everything
mvn clean test

# Run specific test class
mvn clean test -Dtest=HomePageTest
mvn clean test -Dtest=DreamsDiaryTest
mvn clean test -Dtest=CrossPageValidationTest
mvn clean test -Dtest=AIClassificationTest

# Generate Allure report
mvn allure:serve
```

## AI Classification (Bonus)

The classifier has two modes:
1. **OpenAI mode** - uses GPT-3.5-turbo when API key is set
2. **Fallback mode** - uses keyword-based sentiment analysis when no API key is available

This way the tests always run regardless of environment. The fallback uses positive/negative keyword matching to classify dream names.

## Design Notes

- **POM pattern** keeps page locators separate from test logic, making it easier to update if the UI changes
- **Cross-page tests** verify diary data matches the summary stats, not just testing pages in isolation  
- **SoftAssert** used in multi-row validations so all failures are collected before reporting
- **Screenshots** captured automatically on test failure and attached to Allure reports

## Author

Mallela Jithendra - mallelajithendra2004@gmail.com
