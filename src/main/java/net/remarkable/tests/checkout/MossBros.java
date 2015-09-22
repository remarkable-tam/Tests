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

public class MossBros {

    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.private.browsing.autostart", true);
        driver = new FirefoxDriver(profile);
        driver.manage().window().maximize();
    }

    @Test
    public void mossBros() throws IOException, MessagingException {

        String configName = "MossBros";
        String testName = "Moss Bros Checkout";

        Config setUp = new Config(configName, testName, driver);
        Logger log = setUp.getLog();
        SendMessages msg = setUp.getMsg();
        String cardError = setUp.getError();

		WebDriverWait wait = new WebDriverWait(driver, 10);

        log.add("Starting test", false);

        try {

            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            String[] products = { "965460815", "965460807", "965462213", "965462204", "965284601", "965284813",
                    "965461322", "965461501", "965461414", "965461809", "965326003", "965570214", "965326018",
                    "965461219", "965569719", "965461263", "965570119", "965461204", "965461291", "965326071",
                    "965569909", "965326002", "965570798", "965326009", "965570509", "965284109", "965570137",
                    "965570534", "965446515", "965284009", "965434715", "965284118", "965446709", "965434815",
                    "965569918", "965446415", "965446307", "965435715", "965570004", "965570209", "965569801",
                    "965461619", "965570377", "965569709", "965284001", "965435817", "965457098", "965489709",
                    "965446621", "965570991", "965569704", "965569891", "965570819", "965570414", "965461292",
                    "965570418", "965570201", "965570413" };

            driver.get("http://www.moss.co.uk/");
            log.add("Go to site", true);
            driver.findElement(By.id("search-query")).clear();

            log.add("Trying product with ID " + products[0], false);
            driver.findElement(By.id("search-query")).sendKeys(products[0]);
            log.add("Send keys to search", true);
            driver.findElement(By.id("search-query")).submit();

            //Attempt to add to bag
            log.add("Add to Bag", true);
            driver.findElement(By.className("addToBag")).click();

            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.className("basketStatus"))));

            // Checks if product can not be found. Iterate through array until a product is found.
            if (driver.findElement(By.cssSelector(".basketStatus > span")).getText()
                    .contains("Sorry. We have no stock of that item at the moment.")) {
                for (int i = 1; i < products.length; i++) {
                    driver.findElement(By.id("search-query")).clear();

                    log.add("Trying product with ID " + products[i], false);
                    driver.findElement(By.id("search-query")).sendKeys(products[i]);
                    log.add("Send keys to search", true);
                    driver.findElement(By.id("search-query")).submit();

                    log.add("Add to Bag", true);
                    driver.findElement(By.className("addToBag")).click();

                    // Check if product is found, exit loop if present
                    if (!(driver.findElement(By.cssSelector(".basketStatus > span")).getText()
                            .contains("Sorry. We have no stock of that item at the moment."))) {
                        break;
                    } else if (i == (products.length - 1)) {
                        log.add("No more products left in array\n\nTried: " + Arrays.toString(products), false);
                        msg.send("No more products left in array<br /><br />Tried products: " + Arrays.toString(products));
                        Assert.fail("No more products left in array");
                    }
                }
            }

            WebElement payNow = driver.findElements(By.cssSelector("div.addBagConfirmation > div > a.orangeButton")).get(0);

            // Pay now
            log.add("Pay Now", true);
            payNow.click();

            // Checkout
            log.add("Go to Checkout", true);
            driver.findElements(By.cssSelector(".viewBasketContainer > a")).get(1).click();

            // Login
            driver.findElements(By.name("Email")).get(0).clear();
            driver.findElements(By.name("Email")).get(0).sendKeys("checkouttester@remarkable.net");
            log.add("Enter username", true);
            driver.findElements(By.name("Password")).get(0).clear();
            driver.findElements(By.name("Password")).get(0).sendKeys("checkouttester");
            log.add("Enter password", true);
            driver.findElement(By.xpath("//*[@id='LoginForm']/div/div/input")).click();

            // Go to Payment
            log.add("Continue", true);
            driver.findElement(By.className("btn-topRight")).click();


            // Enter payment details and submit
            driver.findElement(By.id("card.cardNumber")).clear();
            driver.findElement(By.id("card.cardNumber")).sendKeys("5555555555555555");
            log.add("Enter card number", true);

            driver.findElement(By.id("card.cardHolderName")).clear();
            driver.findElement(By.id("card.cardHolderName")).sendKeys("Checkout Tester");
            log.add("Enter card holder", true);

            Select expiryMonth = new Select(driver.findElement(By.id("card.expiryMonth")));
            expiryMonth.selectByVisibleText("05");
            log.add("Select expiry month", true);
            Select expiryYear = new Select(driver.findElement(By.id("card.expiryYear")));
            expiryYear.selectByVisibleText("2017");
            log.add("Select expiry year", true);

            driver.findElement(By.id("card.cvcCode")).clear();
            driver.findElement(By.id("card.cvcCode")).sendKeys("123");
            log.add("Enter security code", true);

            driver.findElement(By.className("paySubmit")).click();

			String errorText = driver.findElement(By.id("errorFrameValidationErrors")).getText();
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
