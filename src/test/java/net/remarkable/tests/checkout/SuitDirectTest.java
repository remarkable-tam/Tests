package net.remarkable.tests.checkout;

import net.remarkable.Logger;
import net.remarkable.SendMessages;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SuitDirectTest {

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
    public void suitDirect() throws IOException, MessagingException {

        String configName = "SuitDirect";
        String testName = "Suit Direct Checkout";

        Config setUp = new Config(configName, testName, driver);
        Logger log = setUp.getLog();
        SendMessages msg = setUp.getMsg();
        String cardError = setUp.getError();

        log.add("Starting test", false);

        try {

            WebDriverWait wait = new WebDriverWait(driver, 30);

            String[] products = {"TE910975", "TE910976", "TE910977", "TE910978", "SC910787", "AS910947", "SC910786",
                    "SC910905", "SC910792", "SC910900", "SC910902", "SC910901", "TE9140450", "LH920678", "TE920979",
                    "LH920448", "TE920980", "SC910790", "SC910908", "SC910788", "SC910911", "SC910913", "SC910789",
                    "SC910912", "ST940959", "ST940960", "ST940961", "ST940962", "TE960883", "LH940888", "LH980884",
                    "TE960881", "LH980886", "ST960937", "9222107", "SC910906", "ST960963", "OC511011", "OC511012",
                    "OC511013", "OC511015", "OC511016", "BR930951", "LH970942", "RG970921", "LH970943", "RG970922",
                    "AS970945", "9850006", "0040300", "0040301", "LH910913", "TE910912", "TE910915", "0037657",
                    "0037661", "0037666", "TE910917", "TE910795", "TE910916", "TE910914", "LH910616", "WH510426",
                    "WH910425", "LH910967", "LH910968", "TE910970", "TE910971", "TE910972", "TE910973", "TE910974"};

            driver.get("https://www.suitdirect.co.uk/");
            log.add("Get site", true);

            driver.findElement(By.name("search")).clear();
            log.add("Clear search", true);

            log.add("Trying product with ID " + products[0], false);
            driver.findElement(By.name("search")).sendKeys(products[0]);
            log.add("Send keys to search", true);

            driver.findElement(By.name("search")).submit();
            log.add("Submit search", true);

            // Checks if product can not be found. Iterate through array until a product is found.
            if (driver.findElement(By.className("itemListContainer")).getText()
                    .contains("Sorry no products were found.")) {
                for (int i = 1; i < products.length; i++) {
                    driver.findElement(By.name("search")).clear();
                    log.add("Clear search", true);

                    log.add("Trying product with ID " + products[i], false);
                    driver.findElement(By.name("search")).sendKeys(products[i]);
                    log.add("Send keys to search", true);

                    driver.findElement(By.name("search")).submit();
                    log.add("Submit search", true);

                    // Check if product is found, exit loop if present
                    if (!(driver.findElement(By.className("itemListContainer")).getText()
                            .contains("Sorry no products were found."))) {
                        break;
                    }  else if (i == (products.length - 1)) {
                        log.add("No more products left in array\n\nTried: " + Arrays.toString(products), false);
                        msg.send("No more products left in array<br /><br />Tried products: " + Arrays.toString(products));
                        Assert.fail("No more products left in array");
                    }
                }
            }

            // Add to Bag and go to Basket
            driver.findElement(By.cssSelector("div.itemImage > a")).click();
            log.add("Click product", true);
            driver.findElement(By.className("btnAddToBag")).click();
            log.add("Add to bag", true);

            // Wait 5 seconds max for modal to be clickable, then proceed
            WebElement modal = driver.findElement(By.xpath("//*[@id='cboxLoadedContent']/div/div/div/a[1]"));
            log.add("Go to Basket", true);
            wait.until(ExpectedConditions.elementToBeClickable(modal));
            modal.click();

            // Checkout
            WebElement veModal = driver.findElement(By.id("WindowCloseBtn"));

            if (veModal.isDisplayed()) {
                log.add("Close modal if present", true);
                veModal.click();
            }

            driver.findElement(By.className("bigbutton")).click();
            log.add("Checkout", true);

            // Login
            driver.findElement(By.id("content_txtUserName")).clear();
            driver.findElement(By.id("content_txtUserName")).sendKeys("checkouttester@remarkable.net");
            log.add("Enter username", true);
            driver.findElement(By.id("content_txtPassword")).clear();
            driver.findElement(By.id("content_txtPassword")).sendKeys("checkouttester");
            log.add("Enter password", true);
            driver.findElement(By.id("content_btnLogin")).click();
            log.add("Login", true);

            // Go to Payment
            driver.findElement(By.id("content_chkAgree")).click();
            log.add("Tick T&Cs box", true);
            driver.findElement(By.id("content_LinkButton1")).click();
            log.add("Pay By Card", true);

            driver.switchTo().frame(driver.findElement(By.cssSelector(".adminbox > iframe")));

            // Enter payment details and submit
            driver.findElement(By.id("inputCardNumber")).clear();
            driver.findElement(By.id("inputCardNumber")).sendKeys("5404000000000043");
            log.add("Enter card number", true);

            Select expiryMonth = new Select(driver.findElement(By.id("expiryMonth")));
            expiryMonth.selectByVisibleText("05");
            log.add("Choose expiry month", true);
            Select expiryYear = new Select(driver.findElement(By.id("expiryYear")));
            expiryYear.selectByVisibleText("2017");
            log.add("Choose expiry year", true);

            driver.findElement(By.id("inputSecurity")).clear();
            driver.findElement(By.id("inputSecurity")).sendKeys("123");
            log.add("Enter security code", true);

            driver.findElement(By.id("proceedButton")).click();
            log.add("Proceed", true);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("formCardDetails")));

            log.add("Wait for error message", true);

            String errorText = driver.findElement(By.id("formCardDetails")).getText();
            Boolean checkExpected = errorText.contains(cardError);

            if (!checkExpected) {
                log.add("Expected error not found", true);
                msg.send("<b>Expected text could not be found.<br /><br />Expected:</b> " + cardError + "<br /><br /><b>Returned:</b> " + errorText);
                Assert.fail("Expected text could not be found");
            } else {
                log.delScr();
                log.add("Expected card error was found - screenshots deleted", false);
            }

        } catch (Exception e) {
            String eS = e.toString();
            log.add("Exception found", true);
            log.add(eS, false);

            msg.send("<b>An exception was found</b><br /><br /> " + eS);
            Assert.fail("An exception was found");
        }

        log.add("Test finished\n", false);
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
    }

}
