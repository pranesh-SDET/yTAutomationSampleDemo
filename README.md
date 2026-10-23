# YouTube UI Automation Suite

[![Java 11](https://img.shields.io/badge/Java-11-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Selenium WebDriver 4.21.0](https://img.shields.io/badge/Selenium_WebDriver-4.21.0-43B02A?logo=selenium&logoColor=white)](https://www.selenium.dev/)
[![TestNG 7.7.0](https://img.shields.io/badge/TestNG-7.7.0-FF6C37?logo=testng&logoColor=white)](https://testng.org/)
[![Gradle 7.5.1](https://img.shields.io/badge/Gradle-7.5.1-02303A?logo=gradle&logoColor=white)](https://gradle.org/)
[![Apache POI](https://img.shields.io/badge/Apache_POI-5.2.3-D22128?logo=apache&logoColor=white)](https://poi.apache.org/)

An automated end-to-end testing suite for complex web workflows on YouTube. This project showcases the **Facade / Wrapper Design Pattern**, responsive sidebar drawer synchronization, horizontal carousel traversal across lazy-loaded shelves, resilient multi-attribute XPath locators, and automated data-driven testing via Apache POI[cite: 22, 23, 24, 25].

---

## Architecture & Design Patterns

### 1. Wrapper / Facade Pattern
Direct browser interactions are decoupled from test scripts into reusable utility methods (`Wrappers.java`)[cite: 1]. This abstracts away `WebDriverWait` synchronization, DOM polling, scroll-into-view triggers, and stale-element recovery, ensuring tests remain declarative, clean, and maintainable[cite: 1].

### 2. Responsive SPA Navigation & Viewport Management
* **Dynamic Sidebar Recovery (`ensureGuideMenuIsOpen`):** YouTube frequently collapses into a mini-guide on varying display resolutions. The framework actively detects collapsed states and interacts with the hamburger icon (☰) to expose target navigation links (Movies, Music, News, About).
* **Two-Tier Event Dispatching:** Employs explicit element interaction with an automatic JavaScript fallback (`arguments[0].click()`) when elements are partially obscured by sticky topbars or floating banners[cite: 1, 2].
* **Precise Viewport Alignment:** Uses `{behavior: 'smooth', block: 'center'}` to visually anchor lazy-loaded shelves (e.g., Top Selling, India's Biggest Hits) in the middle of the viewport prior to horizontal pagination[cite: 22, 23, 24].

### 3. Carousel Traversal & Dynamic Metric Sanitization
* **Horizontal Carousel Iteration (`clickTillUnclickable`):** Programmatically advances horizontal media carousels to the extreme right edge by listening to button visibility and disabled states[cite: 22, 31].
* **Metric String Parser (`convertToNumericValue`):** Dynamically parses internationalized view, like, and song counts (e.g., "10M views", "1.2 Cr views", "500K", "50 songs"), stripping alphabetical suffixes, commas, and whitespace into numerical primitives (`long`, `int`)[cite: 29, 30].
* **Non-Blocking Soft Assertions:** Utilizes TestNG's `SoftAssert` to inspect multiple attributes per card (such as category classification and maturity badges) without terminating the test execution on the first mismatch[cite: 22, 24].

### 4. Data-Driven Testing (DDT) via Apache POI
Decouples test execution parameters from test code. Leverages TestNG `@DataProvider` wired to `ExcelReaderUtil` to ingest test datasets from `src/test/resources/data.xlsx`, executing parameterized search and view-accumulation queries iteratively[cite: 25].

---

## Test Scenarios Covered

| Test Case | Module / Feature | Validation & Assertions |
| :--- | :--- | :--- |
| **`testCase01`** | **Home Navigation & About Page** | Asserts landing on `youtube.com`, navigates via sidebar to the About page, validates target URL redirect, and extracts screen mission statement paragraphs[cite: 21]. |
| **`testCase02`** | **Movies & TV: Top Selling Shelf** | Navigates to Movies / Films, centers the Top Selling shelf, scrolls to the extreme right, and applies `SoftAssert` on maturity rating (`A` / `U/A`) and genre existence (Comedy, Animation, Drama, Action)[cite: 22]. |
| **`testCase03`** | **Music: India's Biggest Hits** | Navigates to Music, advances the 1st playlist section carousel horizontally, captures playlist metadata, and validates that track count is $\le 50$[cite: 23, 24]. |
| **`testCase04`** | **News: Latest News Posts** | Navigates to News, parses the first 3 community news cards, prints headers and body text, and calculates the cumulative vote/like tally with fallback for unrated posts[cite: 24]. |
| **`testCase05`** | **Data-Driven View Accumulation** | Ingests queries from `data.xlsx` (Movies, Music, Games, India, UK) via `@DataProvider`, executing automated infinite scrolling until accumulated video views surpass $10\text{ Cr}$ ($10^8$ views)[cite: 25]. |

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
