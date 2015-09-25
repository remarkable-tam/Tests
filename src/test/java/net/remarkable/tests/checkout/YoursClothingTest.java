package net.remarkable.tests.checkout;

import net.remarkable.Logger;
import net.remarkable.SendMessages;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class YoursClothingTest {

    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.private.browsing.autostart", true);
        driver = new FirefoxDriver(profile);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    @Test
    public void yoursClothingUS() throws IOException, MessagingException {
        String configName = "YoursClothing";
        String testName = "Yours Clothing US Checkout";

        Config setUp = new Config(configName, testName, driver);
        Logger log = setUp.getLog();
        SendMessages msg = setUp.getMsg();
        String cardError = setUp.getError();

        WebDriverWait wait = new WebDriverWait(driver, 30);

        log.add("Starting test for Yours US", false);

        try {

            driver.get("http://www.yoursclothing.com");
            log.add("Go to site", true);
            driver.findElement(By.id("search-query")).clear();
            driver.findElement(By.id("search-query")).sendKeys("bracelet");

            log.add("Send keys to search", true);
            driver.findElement(By.id("search-query")).submit();

            log.add("Click product", true);
            driver.findElement(By.cssSelector(".listing-items > .row > div:nth-of-type(1) > div > div > a > img")).click();

            // Add to Bag and go to Basket
            log.add("Add to bag", true);
            driver.findElement(By.className("addToBag")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated((By.className("goToBasket"))));

            log.add("Go to Basket", true);
            driver.findElement(By.className("goToBasket")).click();

            // Check for "No Time?" pop up
            if (driver.findElements(By.className("ve-bounceloop")).size() > 0) {
                driver.findElement(By.className("close-button")).click();
            }

            // Checkout
            log.add("Go to checkout", true);
            driver.findElements(By.className("btn-secondary")).get(0).click();

            // Check for "No Time?" pop up
            if (driver.findElements(By.className("ve-bounceloop")).size() > 0) {
                driver.findElement(By.className("close-button")).click();
            }

            // Login
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys("checkouttester@remarkable.net");
            log.add("Enter username", true);
            driver.findElement(By.id("password")).clear();
            driver.findElement(By.id("password")).sendKeys("checkouttester");
            log.add("Enter password", true);
            driver.findElement(By.name("btnSubmitOption1")).click();
            log.add("Login", true);

            // Enter payment details and submit
            driver.findElement(By.className("accordion-toggle")).click();
            log.add("Add new card", true);
            driver.findElement(By.id("txtCardNumber")).clear();
            driver.findElement(By.id("txtCardNumber")).sendKeys("5404000000000043");
            log.add("Enter card number", true);

            driver.findElement(By.id("txtCardHolder")).clear();
            driver.findElement(By.id("txtCardHolder")).sendKeys("Checkout Tester");
            log.add("Enter card holder", true);

            Select expiryMonth = new Select(driver.findElement(By.id("drpEndDateMonth")));
            expiryMonth.selectByVisibleText("05");
            log.add("Choose expiry month", true);

            Select expiryYear = new Select(driver.findElement(By.id("drpEndDateYear")));
            expiryYear.selectByVisibleText("2017");
            log.add("Choose expiry year", true);

            driver.findElement(By.id("txtCV2")).clear();
            driver.findElement(By.id("txtCV2")).sendKeys("123");
            log.add("Enter security number", true);

            driver.findElement(By.xpath("//*[@id='ajaxTotals']/div/div[1]/div/div[2]/button")).sendKeys(Keys.HOME);
            driver.findElement(By.xpath("//*[@id='ajaxTotals']/div/div[1]/div/div[2]/button")).click();
            log.add("Pay now", true);

            String errorText = driver.findElement(By.className("alert")).getText();
            Boolean checkExpected = errorText.contains(cardError);

            if (!checkExpected) {
                log.add("Expected error not found", true);
                msg.send("<b>Expected text could not be found.<br /><br />Expected:</b> " + cardError + "<br /><br /><b>Returned:</b> " + errorText);
                Assert.fail("Expected text was not found");
            } else {
                log.delScr();
                log.add("Expected card error was found - screenshots deleted", false);
            }

        } catch (Exception e) {
            String eS = e.toString();
            log.add("Exception found", true);
            log.add(eS, false);
            msg.send("<b>An exception was found</b> " + eS);
            Assert.fail("An exception was found");
        }

        log.add("Test finished\n", false);
    }

    @Test
    public void badRhino() throws IOException, MessagingException {

        String configName = "YoursClothing";
        String testName = "Bad Rhino Checkout";

        Config setUp = new Config(configName, testName, driver);
        Logger log = setUp.getLog();
        SendMessages msg = setUp.getMsg();
        String cardError = setUp.getError();

        WebDriverWait wait = new WebDriverWait(driver, 30);

        log.add("Starting test for Bad Rhino", false);

        try {

            driver.get("http://www.badrhino.com");
            log.add("Go to site", true);
            driver.findElement(By.id("search-query")).clear();
            driver.findElement(By.id("search-query")).sendKeys("watch");

            log.add("Send keys to search", true);
            driver.findElement(By.id("search-query")).submit();

            log.add("Click product", true);
            driver.findElement(By.cssSelector(".listing-items > .row > div:nth-of-type(1) > div > div > a > img")).click();

            // Add to Bag and go to Basket
            log.add("Add to bag", true);
            driver.findElement(By.className("addToBag")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated((By.className("goToBasket"))));

            log.add("Go to Basket", true);
            driver.findElement(By.className("goToBasket")).click();

            // Check for "No Time?" pop up
            if (driver.findElements(By.className("ve-bounceloop")).size() > 0) {
                driver.findElement(By.className("close-button")).click();
            }

            // Checkout
            log.add("Go to checkout", true);
            driver.findElements(By.className("btn-secondary")).get(0).click();

            // Check for "No Time?" pop up
            if (driver.findElements(By.className("ve-bounceloop")).size() > 0) {
                driver.findElement(By.className("close-button")).click();
            }

            // Login
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys("checkouttester@remarkable.net");
            log.add("Enter username", true);
            driver.findElement(By.id("password")).clear();
            driver.findElement(By.id("password")).sendKeys("checkouttester");
            log.add("Enter password", true);
            driver.findElement(By.name("btnSubmitOption1")).click();
            log.add("Login", true);

            // Enter payment details and submit
            driver.findElement(By.className("accordion-toggle")).click();
            log.add("Add new card", true);
            driver.findElement(By.id("txtCardNumber")).clear();
            driver.findElement(By.id("txtCardNumber")).sendKeys("5404000000000043");
            log.add("Enter card number", true);

            driver.findElement(By.id("txtCardHolder")).clear();
            driver.findElement(By.id("txtCardHolder")).sendKeys("Checkout Tester");
            log.add("Enter card holder", true);

            Select expiryMonth = new Select(driver.findElement(By.id("drpEndDateMonth")));
            expiryMonth.selectByVisibleText("05");
            log.add("Choose expiry month", true);

            Select expiryYear = new Select(driver.findElement(By.id("drpEndDateYear")));
            expiryYear.selectByVisibleText("2017");
            log.add("Choose expiry year", true);

            driver.findElement(By.id("txtCV2")).clear();
            driver.findElement(By.id("txtCV2")).sendKeys("123");
            log.add("Enter security number", true);

            driver.findElement(By.xpath("//*[@id='ajaxTotals']/div/div[1]/div/div[2]/button")).sendKeys(Keys.HOME);
            driver.findElement(By.xpath("//*[@id='ajaxTotals']/div/div[1]/div/div[2]/button")).click();
            log.add("Pay now", true);

            String errorText = driver.findElement(By.className("alert")).getText();
            Boolean checkExpected = errorText.contains(cardError);

            if (!checkExpected) {
                log.add("Expected error not found", true);
                msg.send("<b>Expected text could not be found.<br /><br />Expected:</b> " + cardError + "<br /><br /><b>Returned:</b> " + errorText);
                Assert.fail("Expected text was not found");

            } else {
                log.delScr();
                log.add("Expected card error was found - screenshots deleted", false);
            }

        } catch (Exception e) {
            String eS = e.toString();
            log.add("Exception found", true);
            log.add(eS, false);
            msg.send("<b>An exception was found</b> " + eS);
            Assert.fail("An exception was found");
        }

        log.add("Test finished\n", false);
    }

    @Test
    public void yoursClothingUK() throws IOException, MessagingException {

        String configName = "YoursClothing";
        String testName = "Yours Clothing UK Checkout";

        Config setUp = new Config(configName, testName, driver);
        Logger log = setUp.getLog();
        SendMessages msg = setUp.getMsg();
        String cardError = setUp.getError();

        WebDriverWait wait = new WebDriverWait(driver, 30);

        log.add("Starting test for Yours UK", false);

        try {

            driver.get("http://www.yoursclothing.co.uk");
            log.add("Go to site", true);
            driver.findElement(By.id("search-query")).clear();
            driver.findElement(By.id("search-query")).sendKeys("bracelet");

            log.add("Send keys to search", true);
            driver.findElement(By.id("search-query")).submit();

            log.add("Click product", true);
            driver.findElement(By.cssSelector(".listing-items > .row > div:nth-of-type(1) > div > div > a > img")).click();

            // Add to Bag and go to Basket
            log.add("Add to bag", true);
            driver.findElement(By.className("addToBag")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated((By.className("goToBasket"))));

            log.add("Go to Basket", true);
            driver.findElement(By.className("goToBasket")).click();

            // Check for "No Time?" pop up
            if (driver.findElements(By.className("ve-bounceloop")).size() > 0) {
                driver.findElement(By.className("close-button")).click();
            }

            // Checkout
            log.add("Go to checkout", true);
            driver.findElements(By.className("btn-secondary")).get(0).click();

            // Check for "No Time?" pop up
            if (driver.findElements(By.className("ve-bounceloop")).size() > 0) {
                driver.findElement(By.className("close-button")).click();
            }

            // Login
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys("checkouttester@remarkable.net");
            log.add("Enter username", true);
            driver.findElement(By.id("password")).clear();
            driver.findElement(By.id("password")).sendKeys("checkouttester");
            log.add("Enter password", true);
            driver.findElement(By.name("btnSubmitOption1")).click();
            log.add("Login", true);

            // Enter payment details and submit
            driver.findElement(By.className("accordion-toggle")).click();
            log.add("Add new card", true);
            driver.findElement(By.id("txtCardNumber")).clear();
            driver.findElement(By.id("txtCardNumber")).sendKeys("5404000000000043");
            log.add("Enter card number", true);

            driver.findElement(By.id("txtCardHolder")).clear();
            driver.findElement(By.id("txtCardHolder")).sendKeys("Checkout Tester");
            log.add("Enter card holder", true);

            Select expiryMonth = new Select(driver.findElement(By.id("drpEndDateMonth")));
            expiryMonth.selectByVisibleText("05");
            log.add("Choose expiry month", true);

            Select expiryYear = new Select(driver.findElement(By.id("drpEndDateYear")));
            expiryYear.selectByVisibleText("2017");
            log.add("Choose expiry year", true);

            driver.findElement(By.id("txtCV2")).clear();
            driver.findElement(By.id("txtCV2")).sendKeys("123");
            log.add("Enter security number", true);

            driver.findElement(By.xpath("//*[@id='ajaxTotals']/div/div[1]/div/div[2]/button")).sendKeys(Keys.HOME);
            driver.findElement(By.xpath("//*[@id='ajaxTotals']/div/div[1]/div/div[2]/button")).click();
            log.add("Pay now", true);

            String errorText = driver.findElement(By.className("alert")).getText();
            Boolean checkExpected = errorText.contains(cardError);

            if (!checkExpected) {
                log.add("Expected error not found", true);
                driver.get("http://www.yoursclothing.co.uk/id.aspx");
                String server = driver.findElement(By.tagName("body")).getText();
                log.add(server, false);
                msg.send("<b>Expected text could not be found.<br /><br />Expected:</b> " + cardError + "<br /><br /><b>Returned:</b> " + errorText + "<br /><br /><b>ID:</b> " + server);
                Assert.fail("Expected text could not be found");

            } else {
                log.delScr();
                log.add("Expected card error was found - screenshots deleted", false);
            }

        } catch (Exception e) {
            String eS = e.toString();
            log.add("Exception found", true);

            driver.get("http://www.yoursclothing.co.uk/id.aspx");
            String server = driver.findElement(By.tagName("body")).getText();
            log.add(eS + "\n\n" + server, false);
            msg.send("<b>An exception was found</b> " + eS + "<br /><br /><b>ID</b>: " + server);
            Assert.fail("An exception was found");
        }

        log.add("Test finished\n", false);
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
    }

}
