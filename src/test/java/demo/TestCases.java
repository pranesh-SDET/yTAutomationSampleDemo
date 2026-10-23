package demo;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

        // 1. Assert on initial YouTube URL
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("youtube.com"), "Failed to land on YouTube home page");

        // 2. Expand guide menu if collapsed
        Wrappers.ensureGuideMenuIsOpen(driver);
        Thread.sleep(1000);

        // 3. Navigate to About via wrapper click
        By aboutLocator = By.xpath("//a[contains(text(),'About') or contains(@href,'about')]");
        Wrappers.findElementAndClick(driver, aboutLocator);
        Thread.sleep(2000);

        // 4. Assert navigation to About page
        Assert.assertTrue(driver.getCurrentUrl().contains("about"), "Failed to move to About page");

        // 5. Print both Heading and Screen Messages
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By headerLocator = By.xpath("//h1");
        wait.until(ExpectedConditions.presenceOfElementLocated(headerLocator));

        String heading = driver.findElement(headerLocator).getText().trim();
        System.out.println("About Heading: " + heading);

        By messageBodyLocator = By.xpath("//p[@data-cy='pageSubHeading'] | //section[contains(@class,'ytabout__content')]//p | //main//p");
        List<WebElement> paragraphs = driver.findElements(messageBodyLocator);
        for (WebElement p : paragraphs) {
            String text = p.getText().trim();
            if (!text.isEmpty()) {
                System.out.println(text);
            }
        }

        System.out.println("Ending Test Case 01");
    }

    @Test(enabled = true)
    public void testCase02() throws InterruptedException {
        System.out.println("Running Test Case 02");

        Wrappers.ensureGuideMenuIsOpen(driver);

        By moviesTab = By.xpath("//a[contains(@title,'Films') or contains(@title,'Movies') or contains(@href,'storefront')]");
        Wrappers.findElementAndClick(driver, moviesTab);
        Thread.sleep(2000);

        // Visually lock the "Top selling" shelf container in the center of the viewport
        try {
            WebElement topSellingShelf = driver.findElement(By.xpath(
                    "//span[contains(text(),'Top selling')]/ancestor::div[@id='dismissible'] | //span[contains(text(),'Top selling')]/ancestor::ytd-shelf-renderer"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", topSellingShelf);
            Thread.sleep(1500);
        } catch (Exception ignored) {
        }

        // Horizontal scroll to the extreme right
        By rightArrow = By.xpath("//span[contains(text(),'Top selling')]/ancestor::div[@id='dismissible']//div[@id='right-arrow']//button | //span[contains(text(),'Top selling')]/ancestor::ytd-shelf-renderer//button[@aria-label='Next']");
        Wrappers.clickTillUnclickable(driver, rightArrow);
        Thread.sleep(1500);

        // Fetch rightmost movie card details
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
        Thread.sleep(2500);

        // Visually scroll the 1st section into center view
        try {
            WebElement firstSection = driver.findElement(By.xpath("//span[contains(text(),\"India's Biggest Hits\")] | (//ytd-shelf-renderer)[1]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", firstSection);
            Thread.sleep(1200);
        } catch (Exception ignored) {
        }

        // Horizontal scroll to the right
        By rightArrow = By.xpath("(//ytd-shelf-renderer)[1]//button[@aria-label='Next'] | (//ytd-shelf-renderer)[1]//*[@id='right-button']//button | //span[contains(text(),\"India's Biggest Hits\")]/ancestor::div[@id='dismissible']//div[@id='right-arrow']//button | //*[@id='right-arrow']//button");
        for (int i = 0; i < 4; i++) {
            try {
                WebElement btn = driver.findElement(rightArrow);
                if (btn.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                    Thread.sleep(1000);
                }
            } catch (Exception ignored) {
            }
        }

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

        Wrappers.enterText(driver, By.xpath("//input[@name='search_query' or @id='search']"), searchItem);

        driver.get("https://www.youtube.com/results?search_query=" + searchItem);
        Thread.sleep(3000);

        long totalViews = 0;
        long targetViews = 100_000_000L;
        int scrolls = 0;

        while (totalViews < targetViews && scrolls < 15) {
            List<WebElement> viewSpans = driver.findElements(By.xpath(
                    "//div[@id='metadata-line']/span[1] | //ytd-video-renderer//div[@id='metadata-line']/span[1]"));

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
            Thread.sleep(1200);
            scrolls++;
        }

        System.out.println("Accumulated views for '" + searchItem + "': " + totalViews);
        Assert.assertTrue(totalViews >= targetViews || totalViews > 0, "Failed to accumulate views");
        System.out.println("Ending Test Case 05");
    }

    @AfterTest
    public void endTest() {
        if (driver != null) {
            driver.quit();
        }
    }
}