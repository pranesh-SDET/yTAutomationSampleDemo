# YouTube UI Automation Suite

[![Java 11](https://img.shields.io/badge/Java-11-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Selenium WebDriver 4.21.0](https://img.shields.io/badge/Selenium_WebDriver-4.21.0-43B02A?logo=selenium&logoColor=white)](https://www.selenium.dev/)
[![TestNG 7.7.0](https://img.shields.io/badge/TestNG-7.7.0-FF6C37?logo=testng&logoColor=white)](https://testng.org/)
[![Gradle 7.5.1](https://img.shields.io/badge/Gradle-7.5.1-02303A?logo=gradle&logoColor=white)](https://gradle.org/)
[![Apache POI](https://img.shields.io/badge/Apache_POI-5.2.3-D22128?logo=apache&logoColor=white)](https://poi.apache.org/)

An automated end-to-end testing suite for complex web workflows on YouTube. This project showcases the **Facade / Wrapper Design Pattern**, responsive sidebar drawer synchronization, horizontal carousel traversal across lazy-loaded shelves, resilient multi-attribute XPath locators, and automated data-driven testing via Apache POI[cite: 22, 23, 24, 25, 35].

---

## Architecture & Design Patterns

### 1. Wrapper / Facade Pattern
Direct `WebDriver` interactions are decoupled into dedicated static utility methods (`Wrappers.java`)[cite: 1]. This isolates browser synchronization, responsive guide drawer state management, scroll-into-view triggers, and element polling from test execution flows, ensuring declarative and flake-free test code[cite: 1].

### 2. Resilient Interaction Strategies
* **Responsive Sidebar Drawer Handling:** Dynamically evaluates whether YouTube is loaded in mini-guide mode and triggers the hamburger menu (`☰`) to expose navigation items (*Movies*, *Music*, *News*, *About*) before attempting locator interaction.
* **Horizontal Carousel Traversal:** Replaces static pixel scrolling with continuous conditional iteration (`clickTillUnclickable`), cycling the next shelf button until lazy-loaded cards reach the extreme right[cite: 22, 31].
* **Two-Tier Event Dispatching:** Combines standard Selenium user actions with an automatic JavaScript execution fallback (`arguments[0].click()`) whenever elements are obscured by sticky headers or layout overlays[cite: 1, 2].
* **Timestamp & Badge Filtering:** Distinguishes between video duration overlay timestamps (e.g., `2:44:46`) and content rating badges (`A`, `U/A`) using string token validation.

### 3. Dynamic Metric Parsing & Aggregation
* **Metric Suffix Normalization:** Automatically parses alphanumeric view and vote metrics (`K`, `M`, `B`, `Cr`, `views`) into primitive `long` and `int` values for threshold comparison[cite: 29, 30].
* **Data-Driven Parameterization:** Connects TestNG's `@DataProvider` with Apache POI to stream test data from Excel spreadsheets (`data.xlsx`) without hardcoded search terms[cite: 25].
* **Non-Blocking Soft Assertions:** Implements TestNG `SoftAssert` across composite cards to validate genres, ratings, and playlist track counts without prematurely terminating subsequent test steps[cite: 22, 24].

---

## Test Scenarios Covered

| Test Case | Target Flow | Strategy & Assertion |
| :--- | :--- | :--- |
| **`testCase01`** | **YouTube Home & About Page** | Validates the home page landing URL, navigates through the sidebar to the **About** page, and prints the core mission statements to the console[cite: 21]. |
| **`testCase02`** | **Movies "Top Selling" Carousel** | Navigates to **Movies**, scrolls the *Top Selling* shelf to the extreme right, and applies soft assertions to verify the presence of maturity ratings (`A`, `U/A`) and genre classifications[cite: 22]. |
| **`testCase03`** | **Music Playlist Track Limits** | Accesses the **Music** tab, scrolls to the rightmost playlist in the first shelf, extracts the playlist name, and soft-asserts that track counts are $\le 50$[cite: 23, 24]. |
| **`testCase04`** | **News Post Votes Aggregation** | Opens the **News** tab, parses the title and body of the first 3 *Latest News Posts*, and computes the cumulative vote/like count[cite: 24]. |
| **`testCase05`** | **Data-Driven View Accumulation** | Ingests search terms from `data.xlsx` via TestNG `@DataProvider`, iteratively scrolls search results, and aggregates video view counts until reaching the target threshold (10 Cr views)[cite: 25]. |

---

## Project Structure

```text
├── src
│   └── test
│       ├── java
│       │   └── demo
│       │       ├── utils
│       │       │   ├── ExcelDataProvider.java  # TestNG DataProvider bridge
│       │       │   └── ExcelReaderUtil.java    # Apache POI workbook parser
│       │       ├── wrappers
│       │       │   └── Wrappers.java           # Reusable UI actions & metric converters
│       │       └── TestCases.java              # TestNG test scenarios & lifecycle hooks
│       └── resources
│           ├── data.xlsx                       # Data-driven test input spreadsheet
│           └── testng.xml                      # TestNG execution suite definition
├── build.gradle                                # Dependency management & Checkstyle config
├── gradle.properties                           # JVM arguments & runtime parameters
├── gradlew                                     # Gradle wrapper script (Unix)
├── gradlew.bat                                 # Gradle wrapper script (Windows)
└── README.md                                   # Project documentation
