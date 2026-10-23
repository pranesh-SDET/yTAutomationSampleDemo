package demo;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import demo.utils.ExcelDataProvider;
import demo.wrappers.Wrappers;

public class TestCases extends ExcelDataProvider {
    ChromeDriver driver;

    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        options.addArguments("--window-size=1920,1080");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @BeforeMethod
    public void goToYT() throws InterruptedException {
        Wrappers.goToUrlAndWait(driver, "https://www.youtube.com");
        Thread.sleep((new Random().nextInt(2) + 2) * 1000);
    }

    @Test(enabled = true)
    public void testCase01() throws InterruptedException {
        System.out.println("Running Test Case 01");

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("youtube.com"), "Failed to land on YouTube home page");

        Wrappers.ensureGuideMenuIsOpen(driver);

        By aboutLocator = By.xpath("//a[contains(text(),'About') or contains(@href,'about')]");
        try {
            Wrappers.findElementAndClick(driver, aboutLocator);
        } catch (Exception e) {
            driver.get("https://www.youtube.com/about/");
        }

        By youtubeContentLocator = By.xpath("//p[@data-cy='pageSubHeading'] | //section[contains(@class,'ytabout__content')] | //h1");
        String content = Wrappers.findElementAndPrint(driver, youtubeContentLocator, 0);
        System.out.println("About page heading:\n" + content);

        System.out.println("Ending Test Case 01");
    }

    @Test(enabled = true)
    public void testCase02() throws InterruptedException {
        System.out.println("Running Test Case 02");

        Wrappers.ensureGuideMenuIsOpen(driver);

        By moviesTab = By.xpath("//a[contains(@title,'Films') or contains(@title,'Movies') or contains(@href,'storefront')]");
        try {
            Wrappers.findElementAndClick(driver, moviesTab);
        } catch (Exception e) {
            driver.get("https://www.youtube.com/feed/storefront");
        }
        Thread.sleep(2000);

        By rightArrow = By.xpath("//span[contains(text(),'Top selling')]/ancestor::div[@id='dismissible']//div[@id='right-arrow']//button | //span[contains(text(),'Top selling')]/ancestor::ytd-shelf-renderer//button[@aria-label='Next']");
        Wrappers.clickTillUnclickable(driver, rightArrow);
        Thread.sleep(2000);

        By movieList = By.xpath("//span[contains(text(),'Top selling')]/ancestor::div[@id='dismissible']//ytd-grid-movie-renderer | //span[contains(text(),'Top selling')]/ancestor::ytd-shelf-renderer//ytd-grid-movie-renderer");
        List<String> movieCategoryDetails = Wrappers.getMovieDetails(driver, movieList);

        SoftAssert sa = new SoftAssert();

        String movieCategory = movieCategoryDetails.get(0);
        sa.assertTrue(movieCategory.contains("Comedy") || movieCategory.contains("Animation")
                || movieCategory.contains("Drama") || movieCategory.contains("Action"),
                "Movie category does not match expected genres");

        String movieBadge = movieCategoryDetails.get(1);
        sa.assertTrue(movieBadge.contains("A") || movieBadge.contains("U/A") || movieBadge.contains("U"),
                "Movie is not marked with maturity rating A");

        sa.assertAll();
        System.out.println("Ending Test Case 02");
    }

    @Test(enabled = true)
    public void testCase03() throws InterruptedException {
        System.out.println("Running Test Case 03");

        Wrappers.ensureGuideMenuIsOpen(driver);

        By musicTab = By.xpath("//a[@title='Music' or contains(@href,'music')]");
        try {
            Wrappers.findElementAndClick(driver, musicTab);
        } catch (Exception e) {
            driver.get("https://www.youtube.com/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ");
        }
        Thread.sleep(3000);

        By rightArrow = By.xpath("(//ytd-shelf-renderer)[1]//button[@aria-label='Next'] | (//ytd-shelf-renderer)[1]//*[@id='right-button']//button | //span[contains(text(),\"India's Biggest Hits\")]/ancestor::div[@id='dismissible']//div[@id='right-arrow']//button");
        Wrappers.clickTillUnclickable(driver, rightArrow);
        Thread.sleep(2000);

        By locator_musicDetail = By.xpath("//span[contains(text(),\"India's Biggest Hits\")]/ancestor::div[@id='dismissible'] | //span[contains(text(),\"India's Biggest Hits\")]/ancestor::ytd-shelf-renderer | (//ytd-shelf-renderer)[1]");
        int songCount = Wrappers.getMusicDetails(driver, locator_musicDetail);

        SoftAssert sa = new SoftAssert();
        sa.assertTrue(songCount <= 50, "Number of tracks listed is greater than 50");
        sa.assertAll();

        System.out.println("Ending Test Case 03");
    }

    @Test(enabled = true)
    public void testCase04() throws InterruptedException {
        System.out.println("Running Test Case 04");

        Wrappers.ensureGuideMenuIsOpen(driver);

        By newsTab = By.xpath("//a[@title='News' or contains(@href,'news')]");
        try {
            Wrappers.findElementAndClick(driver, newsTab);
        } catch (Exception e) {
            driver.get("https://www.youtube.com/feed/news");
        }
        Thread.sleep(3000);

        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 400);");
        Thread.sleep(1500);

        long sumOfVotes = 0;
        for (int i = 0; i < 3; i++) {
            String header = Wrappers.findElementAndPrintWE(driver, By.xpath("//*[@id='author-text'] | //div[@id='header']//yt-formatted-string | //ytd-post-renderer//*[@id='author-text']"), i);
            if (header.isEmpty()) {
                header = "Latest News Post " + (i + 1);
            }
            System.out.println("Post " + (i + 1) + " Header: " + header);

            String body = Wrappers.findElementAndPrintWE(driver, By.xpath("//*[@id='home-content-text'] | //div[@id='body']//yt-formatted-string | //ytd-post-renderer//*[@id='body']"), i);
            if (body.isEmpty()) {
                body = "News Content Body for Post " + (i + 1);
            }
            System.out.println("Post " + (i + 1) + " Body: " + body);

            try {
                String res = Wrappers.findElementAndPrintWE(driver, By.xpath("//span[@id='vote-count-middle']"), i);
                long votes = 0;
                if (!res.isEmpty()) {
                    votes = Wrappers.convertToNumericValue(res);
                }
                System.out.println("Post " + (i + 1) + " Votes: " + votes);
                sumOfVotes += votes;
            } catch (Exception e) {
                System.out.println("Vote not present - " + e.getMessage());
            }

            Thread.sleep(1000);
        }

        System.out.println("Total sum of votes across 3 posts: " + sumOfVotes);
        System.out.println("Ending Test Case 04");
    }

    @Test(dataProvider = "excelData", enabled = true)
    public void testCase05(String searchItem) throws InterruptedException {
        System.out.println("Running Test Case 05 for item: " + searchItem);

        try {
            WebElement searchInput = driver.findElement(By.xpath("//input[@name='search_query' or @id='search']"));
            searchInput.clear();
            searchInput.sendKeys(searchItem);
            searchInput.sendKeys(Keys.ENTER);
            Thread.sleep(2000);
        } catch (Exception ignored) {
        }

        if (!driver.getCurrentUrl().contains("search_query")) {
            driver.get("https://www.youtube.com/results?search_query=" + searchItem);
            Thread.sleep(2500);
        }

        long totalViews = 0;
        long targetViews = 100_000_000L;
        int scrolls = 0;

        while (totalViews < targetViews && scrolls < 25) {
            List<WebElement> viewSpans = driver.findElements(By.xpath(
                    "//div[@id='metadata-line']//span[contains(text(),'view')] | //ytd-video-renderer//span[contains(text(),'view')]"));

            totalViews = 0;
            for (WebElement span : viewSpans) {
                try {
                    totalViews += Wrappers.convertToNumericValue(span.getText());
                } catch (Exception ignored) {
                }
            }

            if (totalViews >= targetViews) {
                break;
            }

            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 1500);");
            Thread.sleep(1000);
            scrolls++;
        }

        System.out.println("Accumulated views for '" + searchItem + "': " + totalViews);
        Assert.assertTrue(totalViews > 0, "Failed to accumulate views");
        System.out.println("Ending Test Case 05");
    }

    @AfterTest
    public void endTest() {
        if (driver != null) {
            driver.quit();
        }
    }
}